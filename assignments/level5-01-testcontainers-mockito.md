# Level5-1: Testcontainersによる自己完結型テストとMockitoによる単体テスト

- 提示日: 2026-07-12
- 状態: 完了（2026-07-18）

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

**進め方**: ユーザーの依頼により「TDDを意識して」実装。`tdd`スキルの指示に沿って、まずシーム（テスト対象の境界）をユーザーと確認してから着手した。

- シーム1: `MemberService`の公開メソッド（`register`/`findById`）。`MemberRepository`をMockitoでモック化し、Springを起動しない単体テスト
- シーム2: `MemberController`のHTTPエンドポイント（`MockMvc`）。実際のDB（Testcontainers管理のPostgreSQL）を使う統合テスト

**TDDサイクル（シーム1: `MemberServiceTest`、新規作成）**:
1. Red→Green: `registerSavesNewActiveMemberAndReturnsResponse`（登録が成功しACTIVE状態のレスポンスを返す）
2. Red→Green: `findByIdThrowsWhenMemberDoesNotExist`（存在しないIDで例外が投げられる）
3. Red→Green: `findByIdReturnsResponseWhenMemberExists`（存在するIDで正しいレスポンスが返る）

いずれも既存実装（Level3-1で実装済み）に対する特性テストのため、初回から成功（Green）だった。この点はユーザーにも明示している。実行時間は3テスト合計で0.4秒程度。

**シーム2: `MemberControllerTest`のTestcontainers化**:
- `build.gradle`に`org.testcontainers:junit-jupiter`/`org.testcontainers:postgresql`を追加
- `@Testcontainers`+`@Container`で`PostgreSQLContainer`を管理し、`@DynamicPropertySource`で接続情報を動的に注入する形に書き換え
- `docker compose down`でローカルのPostgreSQLコンテナを完全に停止した状態でテストを実行し、Testcontainers単体で自己完結することを実地で確認

**環境トラブルと対応（大きな問題）**:
- このマシンのDocker Desktop（4.80.0、Engine 29.6.1）は`MinAPIVersion`が1.40で、それ未満のAPIリクエストを`Status 400`で拒否する
- Testcontainers（内部の`docker-java`）が画像確認処理で古いAPIバージョン（v1.32）を使おうとし、`client version 1.32 is too old`というエラーでコンテナ起動が失敗
- `curl`で直接ソケットを叩くと正常に応答が返ることから、Testcontainers固有の問題だと切り分けた
- Testcontainersを最新版（1.20.1→1.21.3）に上げても解消せず、最終的に`build.gradle`の`test`タスクに`systemProperty "api.version", "1.41"`を追加することで解消した
- どの設定が実際に必要かを1つずつ削って検証し、この1行だけで十分なことを確認した（当初は環境変数`DOCKER_API_VERSION`や複数の設定を重ねていたが、不要なものは削除した）
- 詳細は[SETUP.md](../SETUP.md)に記録済み

**動作確認結果**: 全21テスト（`MoneyTest`7、`MemberTest`5、`SimpleGreeterTest`1、`PointCalculatorTest`2、`MemberServiceTest`3、`MemberControllerTest`3）が成功。`docker ps -a`でTestcontainers起動のコンテナが実行後きれいに破棄されていることも確認済み。

**設計論点の理解度**:
1. テストごとに独立したDBコンテナを使う理由について「ロック競合」「レコードの意図しない書き換え」という具体的な問題を的確に指摘できた。メンターから「テスト実行順序への非依存」「CI環境でのポータビリティ」を補足
2. Testcontainers vs H2の比較について「本番に近い環境 vs 起動速度」というトレードオフを正確に説明できた。メンターから「SQL方言の違いによる偽陽性/偽陰性」という具体例（今回のFlywayマイグレーションがPostgreSQL向けであることとも関連）を補足
3. テストピラミッドの考え方自体を知らなかったため直接説明した。単体テストと統合テストで「検証しているレイヤーが違う」という位置づけを理解してもらった
4. モックのリスクについて「実態とかけ離れたテストになる」という方向性は正しかったが、メカニズムの説明が抽象的だった。「モックは開発者の仮定であり、実装のバグをモック自体は検知できない」という具体的な仕組みを、実際に書いた`MemberServiceTest`のコード例を使って補足した

**つまずいた点**:
- テストピラミッドという用語・概念自体を知らなかった（前提知識のため直接説明）
- モックのリスクについて、表面的な理解（「本物と違うから危ない」）から、具体的なメカニズム（モックが嘘の前提を提供し続けてしまう）への深掘りが必要だった
