# Level4-1: PostgreSQL + Flywayの導入とMemberのJPA化

- 提示日: 2026-07-06
- 状態: 完了（2026-07-12）

## 学習テーマ
Spring Data JPAとFlywayを使い、これまでインメモリで保持していた`Member`を実際のリレーショナルデータベース（PostgreSQL）に永続化する。

## ゴール
`docker compose`でPostgreSQLをローカルに立て、Flywayでスキーマをバージョン管理しながら、Spring Data JPAで`Member`をDBに保存・取得できるようになる。

## 要件

1. `docker-compose.yml`を作成し、PostgreSQLコンテナをローカルで起動できるようにする。
2. Flywayのマイグレーションファイル（`src/main/resources/db/migration/V1__xxx.sql`のような命名）を作成し、`members`テーブルを定義する。
3. `Member`をJPAで永続化できるようにする（`@Entity`を直接付けるか、JPA専用のクラスを分離するかは設計論点として後述）。
4. これまでインメモリ（`ConcurrentHashMap`）で実装していた`MemberRepository`を、Spring Data JPAの`JpaRepository`を使った実装に置き換える。
5. Level3で作った`POST /members`・`GET /members/{id}`が、実際にPostgreSQLに対して動作することを確認する。
6. アプリの接続先DB情報（ホスト・ポート・DB名・ユーザー名・パスワード）を`application.yml`に設定する。

## 制約
- テーブルのスキーマ変更は、必ずFlywayのマイグレーションファイルを通して行うこと（手動でDBに`CREATE TABLE`を打たない）。
- `application.yml`の`spring.jpa.hibernate.ddl-auto`は`validate`または`none`にすること（Hibernateにテーブルを自動生成させない。理由は設計論点で扱います）。

## 設計上の論点

1. `Member`ドメインクラスに直接`@Entity`を付ける方法と、`MemberJpaEntity`のようなJPA専用のクラスを別に用意し、ドメインの`Member`との間で変換する方法があります。それぞれのメリット・デメリットは何だと思いますか？（ヒント: DTOを分離した理由と似た論点です）
2. なぜFlywayのようなマイグレーションツールを使うのでしょうか？手動で本番DBに`ALTER TABLE`を打つ運用と比べて、何が変わりますか？
3. `spring.jpa.hibernate.ddl-auto=update`（Hibernateにテーブル構造を自動生成・更新させる設定）を本番運用で使うと、どんなリスクがあると思いますか？
4. DBのテーブルには`NOT NULL`制約や一意制約を定義できますが、アプリケーション側（Bean Validationなど）でも同様のチェックをすでに行っています。これは二重管理でしょうか？それぞれの役割の違いは何だと思いますか？

## 実装ヒント（答えにならない程度に）
- `build.gradle`に`spring-boot-starter-data-jpa`、`org.postgresql:postgresql`、`org.flywaydb:flyway-core`（Postgres用は`flyway-database-postgresql`も必要な場合があります）の追加が必要です
- Flywayのマイグレーションファイルは、Spring Bootのバージョンによって`src/main/resources/db/migration/`配下に置くのが規約です
- Spring Data JPAの`Repository`は、インターフェースを定義するだけで実装をSpringが自動生成してくれます（`JpaRepository<エンティティ型, ID型>`を継承するだけ）
- `docker-compose.yml`には、PostgreSQLの公式イメージ、ポート、環境変数（DB名・ユーザー名・パスワード）、データ永続化用のボリュームなどを定義します

## テスト観点
- 単体テスト: これまで通り、Springを起動しないテストは維持する
- 統合テスト: Testcontainers（すでに技術スタックに入っていますが、本格導入はLevel5で扱います。今回は手動での動作確認で構いません）または実際に`docker compose up`した状態で`curl`や既存の`MemberControllerTest`を実行し、DBに保存されることを確認する

## レビュー観点
- ドメインモデルと永続化の関心が分離されているか
- マイグレーションファイルの命名・内容が適切か
- トランザクション境界（`@Transactional`をどこに置くか）を意識できているか
- DB接続情報がハードコードされていないか（Level2で学んだ外部設定の考え方が活きているか）

## 学ぶべき設計原則
永続化の関心の分離、マイグレーション管理、スキーマとアプリケーションコードのバージョン整合性

---

## レビューログ

**実装**: ユーザーの依頼により、要件1〜6をメンター（Claude）が直接実装。実装後、設計論点についてユーザーに問答形式で確認した。

**実装物**:
- `docker-compose.yml`（PostgreSQL、ホスト側ポート`5433`）
- `application.yml`にDB接続情報、`ddl-auto: validate`、Flyway設定を追加
- `db/migration/V1__create_members_table.sql`（`members`テーブル定義）
- `MemberJpaEntity`（`Member`ドメインとは別のJPA専用クラス、`fromDomain`/`toDomain`で相互変換）
- `MemberJpaRepository`（Spring Data JPAの`JpaRepository`）
- `MemberRepository`: 既存の公開API（`save`/`findById`）を維持したまま、内部実装をインメモリからJPA経由に置き換えるアダプタとして書き換え。`MemberService`/`MemberController`は無変更で済んだ
- `Member`ドメインに新規のgetterは不要（Level3-1で追加済みのものを利用）

**環境トラブルと対応**:
- ホストマシンに既存のローカルPostgreSQLプロセスが`127.0.0.1:5432`で稼働しており、Dockerコンテナとポートが衝突。`localhost:5432`への接続が意図せずローカルの既存Postgresにルーティングされ、「role "issuing" does not exist」というエラーで原因特定に時間がかかった。既存サービスを止めるのはリスクがあるため、Docker側のホストポートを`5433`に変更して解決（詳細は[SETUP.md](../SETUP.md)にも追記予定）

**設計論点の理解度**:
1. `MemberJpaEntity`を分離した理由について、当初「JPA以外の永続化手段に変える際の影響範囲を狭められる」という抽象度の高い回答だった。「JPAが要求する引数なしコンストラクタ・リフレクションによるフィールド操作は、`Member`が大事にしてきたカプセル化・不変性の方針と相性が悪い」という具体的な技術的制約を示す質問を通じて、「引数なしコンストラクタでは正しい状態のオブジェクトを作れなくなる」「リフレクションによる外部からのフィールド操作はカプセル化に違反する」と正確に言語化できた
2. Flywayを使う理由について、当初「作業ミスを減らせるから」という回答だった。「複数環境でのバージョン把握」「新メンバーのオンボーディング」という具体的なシナリオを問うことで、「DBスキーマ変更をコードと同様にバージョン管理し、誰の環境でも同じ手順で再現できるようにする」という本質的な理由に到達した
3. `ddl-auto=update`のリスクについて「意図しないタイミングでテーブル構造が変わる」という方向性は正しかったが、具体例（フィールドリネームによる意図しないカラム追加・データ消失）を提示して補足。`validate`が「変更せず、不一致なら起動時エラーで検知する」設定であることも説明
4. アプリ側とDB側のバリデーションの二重管理について、当初「レイヤーが違うから両方に意味がある」という一般論だった。「バッチ処理がAPIを経由せず直接DBに書き込む場合」「DB制約だけでSQL例外がそのままユーザーに見える場合」という2つの具体シナリオを問うことで、「DB制約は最終防衛ライン（Defense in Depth）、アプリ側検証はユーザー向けの適切なエラー体験」と正確に整理できた

**つまずいた点**:
- 設計論点への回答が、抽象的・一般論的な理由付けに留まりやすい傾向がある（Level3までと共通する傾向）。具体的なシナリオ・思考実験を提示することで、本質的な理由への到達を促す進め方が引き続き有効

**既知の課題（次回以降への種）**:
- `MemberControllerTest`（`@SpringBootTest`）が実際に稼働中のDocker/PostgreSQLに依存する状態になっている。Docker未起動だとテストが失敗する脆さがある → Level5でTestcontainersを導入し、テスト実行時に自動でDBコンテナを起動・破棄する構成に置き換える予定
- `MemberJpaEntity.toDomain()`で、永続化されていた`SUSPENDED`/`WITHDRAWN`状態を、`Member`のコンストラクタでいったん`ACTIVE`にしてから`suspend()`/`withdraw()`を呼んで復元している。意味的にはやや歪んだ実装（「今退会させた」のではなく「もともと退会していた」状態の復元のため）。将来的なリファクタリング候補
