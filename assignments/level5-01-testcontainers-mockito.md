# Level5-1: Testcontainersによる自己完結型テストとMockitoによる単体テスト

- 提示日: 2026-07-12
- 状態: 出題済み、提出待ち

## 学習テーマ
Testcontainersでテスト実行時だけDBコンテナを自動起動・破棄する仕組みを導入し、テストの独立性・再現性を高める。あわせてMockitoでリポジトリをモック化した単体テストを書き、統合テストとの使い分けを理解する。

## ゴール
- `MemberControllerTest`が、手動で`docker compose up`しなくても（Docker自体は必要ですが）単独で実行できるようにする
- `MemberService`の単体テストを、Springコンテナを起動せず高速に実行できるようにする

## 背景
Level4-1で、`MemberControllerTest`は実際に起動中のPostgreSQLに依存する状態になりました。これは「Dockerを起動し忘れるとテストが失敗する」という脆さを持っています。Testcontainersは、テスト実行のたびに使い捨てのDBコンテナを自動的に起動・破棄してくれるライブラリで、この問題を解決します。

## 要件

1. `build.gradle`にTestcontainers関連の依存関係（`org.testcontainers:junit-jupiter`、`org.testcontainers:postgresql`）を追加する。
2. `MemberControllerTest`を、`@Testcontainers`と`@Container`を使ってPostgreSQLコンテナをテスト実行時に自動管理する形に書き換える。
3. `@DynamicPropertySource`を使い、Spring Bootの接続先DB設定を、Testcontainersが起動したコンテナの動的なホスト・ポートに合わせる。
4. 新しく`MemberServiceTest`を作成し、`MemberRepository`をMockitoでモック化した単体テストを書く（Springコンテナを一切起動しない）。最低限、以下を検証する。
   - `register`が呼ばれたとき、`MemberRepository.save`が呼ばれ、正しい`MemberResponse`が返る
   - `findById`で存在しないIDを渡すと`MemberNotFoundException`が投げられる

## 制約
- `MemberControllerTest`実行時に、手動で`docker compose up`しておく必要がないこと（Testcontainersが自動でコンテナを管理する）。
- `MemberServiceTest`は`@SpringBootTest`を使わないこと（Springコンテナを起動しない、純粋なMockitoベースの単体テストにする）。

## 設計上の論点

1. なぜテストごとに独立したDBコンテナを使うのでしょうか？複数のテストが同じ既存DBを共有する場合と比べて、どんな問題を防げますか？
2. DBが必要なテストの選択肢として、Testcontainers（本物のPostgreSQLをDockerで起動）と、H2のようなインメモリDB（Javaプロセス内で完結する軽量DB）があります。それぞれのメリット・デメリットは何だと思いますか？
3. `MemberControllerTest`（`MockMvc`+実際のDB）と`MemberServiceTest`（Mockitoによるモック）は、同じ「会員登録」の振る舞いをテストしていますが、役割はどう違うと思いますか？（「テストピラミッド」という考え方も調べてみてください）
4. モックを使ったテストにはリスクもあります。`MemberRepository`をモック化しすぎると、どんな問題が起きると思いますか？（ヒント: モックは「こう呼ばれるはずだ」という思い込みで設定されます。その思い込みが実際の実装とズレていたら？）

## 実装ヒント（答えにならない程度に）
- `spring-boot-starter-test`には、実はすでにMockitoが含まれています（`@Mock`、`Mockito.when(...)`などが使えます）
- `@Testcontainers`と`@Container`は、JUnit5のライフサイクル（テストクラスの前後）にコンテナの起動・停止を統合してくれます
- `@DynamicPropertySource`は、テスト実行時に動的に決まる値（コンテナのランダムなポート番号など）を、Spring Bootの設定値として注入するための仕組みです
- Mockitoでモックを使う場合、`@ExtendWith(MockitoExtension.class)`をテストクラスに付けるのが一般的です

## テスト観点
- `MemberControllerTest`: Dockerさえ起動していれば（`docker compose up`していなくても）、単独で実行して成功すること
- `MemberServiceTest`: 実行時間が`MemberControllerTest`よりも大幅に短いこと（Springコンテナを起動しないため）

## レビュー観点
- Testcontainersの導入によりテストの独立性が確保されているか
- モックの使い方が適切か（過剰なモック化になっていないか）
- 単体テストと統合テストの役割分担が明確か

## 学ぶべき設計原則
テストの独立性・再現性、テストピラミッド、モックの適切な使いどころ

---

## レビューログ

（提出・レビュー後にここへ追記する：学んだこと・つまずいた点・模範解答の要旨）
