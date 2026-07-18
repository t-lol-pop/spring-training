# クラス図（2026-07-12時点）

Level1〜4で作成したクラスの関係を示す。VSCodeでは、Mermaid対応のMarkdownプレビュー拡張機能（例: "Markdown Preview Mermaid Support"）でこのファイルを開くと図として表示できる。

```mermaid
classDiagram
    %% ==== domain パッケージ ====
    class Money {
        -long amount
        -String currency
        +Money(long amount)
        +plus(Money other) Money
        +minus(Money other) Money
        +getAmount() long
    }
    class Member {
        -String id
        -String name
        -LocalDate registeredDay
        -Status currentStatus
        +Member(String id, String name, LocalDate registeredDay)
        +getId() String
        +getName() String
        +getRegisteredDay() LocalDate
        +getCurrentStatus() Status
        +isCardIssuable() boolean
        +suspend() void
        +withdraw() void
    }
    class Status {
        <<enumeration>>
        ACTIVE
        SUSPENDED
        WITHDRAWN
    }
    class InsufficientBalanceException {
        +InsufficientBalanceException(String message)
    }

    %% ==== greeting パッケージ ====
    class Greeter {
        <<interface>>
        +greet(String memberName) String
    }
    class SimpleGreeter {
        +greet(String memberName) String
    }
    class FormalGreeter {
        +greet(String memberName) String
    }
    class GreetingRunner {
        -Greeter greeter
        +run(String args) void
    }

    %% ==== point パッケージ ====
    class PointProperties {
        <<record>>
        +BigDecimal rate
    }
    class PointCalculator {
        -PointProperties pointProperties
        +calculate(long purchaseAmount) long
    }
    class PointRunner {
        -PointCalculator pointCalculator
        +run(String args) void
    }

    %% ==== member パッケージ ====
    class MemberRegisterRequest {
        <<record>>
        +String name
    }
    class MemberResponse {
        <<record>>
        +String id
        +String name
        +LocalDate registeredDay
        +String status
    }
    class ErrorResponse {
        <<record>>
        +String message
    }
    class MemberNotFoundException {
        +MemberNotFoundException(String memberId)
    }
    class MemberController {
        -MemberService memberService
        +register(MemberRegisterRequest request) ResponseEntity
        +findById(String id) MemberResponse
    }
    class MemberExceptionHandler {
        <<RestControllerAdvice>>
        +handleMemberNotFound(MemberNotFoundException ex) ResponseEntity
        +handleValidationError(MethodArgumentNotValidException ex) ResponseEntity
    }
    class MemberService {
        -MemberRepository memberRepository
        +register(MemberRegisterRequest request) MemberResponse
        +findById(String id) MemberResponse
    }
    class MemberRepository {
        -MemberJpaRepository memberJpaRepository
        +save(Member member) Member
        +findById(String id) Optional
    }
    class MemberJpaEntity {
        <<Entity>>
        -String id
        -String name
        -LocalDate registeredDay
        -Status status
        +toDomain() Member
    }
    class MemberJpaRepository {
        <<interface>>
    }

    %% ==== アプリ起動 ====
    class IssuingApplication {
        <<SpringBootApplication>>
        +main(String args) void
    }

    Member *-- Status : has

    SimpleGreeter ..|> Greeter
    FormalGreeter ..|> Greeter
    GreetingRunner --> Greeter : constructor injection

    PointCalculator --> PointProperties : constructor injection
    PointRunner --> PointCalculator : constructor injection

    MemberController --> MemberService : constructor injection
    MemberController ..> MemberRegisterRequest : receives
    MemberController ..> MemberResponse : returns
    MemberExceptionHandler ..> MemberNotFoundException : handles
    MemberExceptionHandler ..> ErrorResponse : returns

    MemberService --> MemberRepository : constructor injection
    MemberService ..> MemberResponse : creates
    MemberService ..> MemberNotFoundException : throws

    MemberRepository --> MemberJpaRepository : constructor injection
    MemberRepository ..> Member : returns
    MemberRepository ..> MemberJpaEntity : uses

    MemberJpaEntity ..> Member : converts to/from

    Money ..> InsufficientBalanceException : throws

    IssuingApplication ..> PointProperties : enables via EnableConfigurationProperties
```

## 補足

- パッケージごとの区切りは、図中の`%%`コメント（コード上の見出し）で表現している（Mermaidの`namespace`構文は環境によって描画に失敗することがあるため使っていない）
- `..|>` は「インターフェースの実装」、`-->`（実線矢印）は「コンストラクタインジェクションによる依存」、`..>`（点線矢印）は「一時的な利用・変換・生成」を表す
- `member`パッケージの依存の流れは `MemberController → MemberService → MemberRepository → MemberJpaRepository` という、レイヤードアーキテクチャの典型的な構造になっている
- `MemberJpaEntity`と`Member`（ドメイン）は、互いに参照し合わず、`MemberJpaEntity`側が一方的に変換責任を持つ形にしている（ドメインをJPAの都合から独立させるため）

### パッケージ対応表

| パッケージ | クラス |
|---|---|
| `com.training.issuing.domain` | `Money`, `Member`, `Status`（`Member`の内部enum）, `InsufficientBalanceException` |
| `com.training.issuing.greeting` | `Greeter`, `SimpleGreeter`, `FormalGreeter`, `GreetingRunner` |
| `com.training.issuing.point` | `PointProperties`, `PointCalculator`, `PointRunner` |
| `com.training.issuing.member` | `MemberRegisterRequest`, `MemberResponse`, `ErrorResponse`, `MemberNotFoundException`, `MemberController`, `MemberExceptionHandler`, `MemberService`, `MemberRepository`, `MemberJpaEntity`, `MemberJpaRepository` |
| `com.training.issuing`（ルート） | `IssuingApplication` |
