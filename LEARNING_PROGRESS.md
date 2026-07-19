# 学習進捗ドキュメント

このファイルはセッションをまたいで学習状況を引き継ぐための記録です。
更新ルールは [CLAUDE.md](./CLAUDE.md) を参照。環境構築手順は [SETUP.md](./SETUP.md) を参照。実装済みクラスの関係は [docs/class-diagram.md](./docs/class-diagram.md)、各クラスの役割・設計理由は [docs/design-notes.md](./docs/design-notes.md) を参照。

---

## 全体ロードマップ

| Level | テーマ | 到達目標 | 状態 |
|---|---|---|---|
| 1 | Java基礎（実践的な書き方） | 不変性・カプセル化・enum・equals/hashCodeを使いこなす | 完了 |
| 2 | Spring Boot基礎 | DI/IoC、Bean、設定の仕組みを理解し最小アプリを起動 | 完了 |
| 3 | REST API設計 | リソース設計、DTO、バリデーション、例外ハンドリング | 完了 |
| 4 | データベース設計 | JPA、Flyway、トランザクション境界、整合性制約 | 完了 |
| 5 | テスト | JUnit5/Mockito/Testcontainers、テスト容易な設計 | 完了 |
| 6 | SOLID原則（重点） | 違反コードのレビュー→改善→模範解答 | 完了 |
| 7 | リファクタリング | Fowlerの手法で既存コードを段階的に改善 | 進行中 |
| 8 | DDD基礎 | Entity/VO/Aggregate/Repository/Domain Service | 未着手 |
| 9 | クリーンアーキテクチャ | レイヤー分離、依存方向、パッケージ設計 | 未着手 |
| 10 | 実践課題 | オーソリ〜クリアリングの一連フローを設計・実装・レビュー | 未着手 |

## 開発アプリケーション全体像

簡易カード発行会社（イシュア）システム。

```
会員(Member) --発行--> カード(Card)
カードで --オーソリ(Authorization)--> 与信枠を仮確保
       --売上確定(Capture)--> 実売上に確定
       --クリアリング(Clearing)--> 精算バッチで会員の請求確定
       --赤伝/黒伝(Reversal/Correction)--> 取消・訂正
       --返金(Refund)--> 会員へ資金を戻す
利用履歴(Transaction History) は全操作の監査ログ
残高(Balance) / 利用可能枠(Available Credit) は常に整合性を保つ
ポイント(Points) は売上確定に連動して付与
冪等性(Idempotency Key) は決済APIの二重実行防止に必須
```

## 技術スタック

Java 21 / Spring Boot 3.x / Gradle / Spring Web / Spring Data JPA / PostgreSQL / Flyway / Validation / JUnit5 / Mockito / Testcontainers / Docker Compose / OpenAPI

---

## 現在地点

- **現在のLevel**: 7（リファクタリング）
- **直近の課題**: [assignments/level7-01-channel-enum-refactor.md](./assignments/level7-01-channel-enum-refactor.md)
- **次のアクション**: ユーザーの実装物を待っている（未提出）
- **Git運用**: 2026-07-18から、課題ごとにブランチを切りPRを作成する運用（詳細は[CLAUDE.md](./CLAUDE.md)）。Issue #4はPR #6として対応・マージ・クローズ済み
- **環境メモ**: `MemberControllerTest`はTestcontainers管理のPostgreSQLで完全に自己完結（`docker compose up`不要）。`MemberServiceTest`はMockitoによる単体テスト（Springコンテナ不使用）。**重要な既知の設定**: `build.gradle`の`test`タスクに`systemProperty "api.version", "1.41"`が必須（このマシンのDocker DesktopとTestcontainersのAPIバージョン不整合を回避するため。詳細は[SETUP.md](./SETUP.md)）

---

## 課題一覧

課題の詳細（要件・論点・ヒント・レビュー結果）は `assignments/` 配下に1課題1ファイルで管理する。
このセクションは進捗を一覧できる目次として使う。

| ID | 課題名 | ファイル | 状態 |
|---|---|---|---|
| Level1-1 | Money / Member クラス設計 | [assignments/level1-01-money-member.md](./assignments/level1-01-money-member.md) | 完了（2026-07-05） |
| Level2-1 | DI/IoCの基礎（Greeter） | [assignments/level2-01-di-basics.md](./assignments/level2-01-di-basics.md) | 完了（2026-07-05） |
| Level2-2 | 外部設定（application.yml）とプロファイル | [assignments/level2-02-external-config.md](./assignments/level2-02-external-config.md) | 完了（2026-07-05） |
| Level3-1 | 会員登録・照会のREST API | [assignments/level3-01-member-rest-api.md](./assignments/level3-01-member-rest-api.md) | 完了（2026-07-06） |
| Level4-1 | PostgreSQL + FlywayとMemberのJPA化 | [assignments/level4-01-jpa-postgres.md](./assignments/level4-01-jpa-postgres.md) | 完了（2026-07-12） |
| Level5-1 | Testcontainers + Mockito | [assignments/level5-01-testcontainers-mockito.md](./assignments/level5-01-testcontainers-mockito.md) | 完了（2026-07-18） |
| Level6-1 | God Objectのレビューとリファクタリング設計 | [assignments/level6-01-god-object-review.md](./assignments/level6-01-god-object-review.md) | 完了（2026-07-18、[Issue #4](https://github.com/t-lol-pop/spring-training/issues/4)はPR #6で解消済み） |
| Level7-1 | `channel`のenum化（Primitive Obsessionの解消） | [assignments/level7-01-channel-enum-refactor.md](./assignments/level7-01-channel-enum-refactor.md) | 出題済み・提出待ち |

---

## 次回課題（予告メモ）

- Level7-1完了後、Level7の別課題（通知の抽象化、`MemberJpaEntity.toDomain()`の再構築ロジック改善など）に進むか、Level8（DDD基礎）へ
