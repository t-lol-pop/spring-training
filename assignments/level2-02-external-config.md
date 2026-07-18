# Level2-2: 外部設定（application.yml）とプロファイルの基礎

- 提示日: 2026-07-05
- 状態: 完了（2026-07-05）

## 学習テーマ
Spring Bootの外部設定の仕組み（`application.yml`、`@ConfigurationProperties`、プロファイル）を理解する。

## ゴール
「ポイント付与率」のような、環境によって変わりうる値をコードにハードコードせず、外部設定ファイルから型安全に読み込めるようになる。また、開発環境・本番環境で異なる設定値を持てる「プロファイル」の仕組みを理解する。

## 要件

1. `src/main/resources/application.yml` に、ポイント付与率を表す設定値を追加する（例: `issuing.point.rate: 0.01` のようなキー）。
2. その設定値を型安全にバインドするクラス（`@ConfigurationProperties`を使ったクラス）を作る。
3. その設定値を使って、購入金額（`Money`または`long`でよい）からポイントを計算するクラス（例: `PointCalculator`）を作り、DIで設定値を受け取れるようにする。
4. `application-dev.yml`と`application-prod.yml`を用意し、それぞれ異なるポイント付与率を設定する。
5. `spring.profiles.active`を切り替えて`./gradlew bootRun`を実行し、プロファイルによって実際にポイント計算の結果が変わることを確認する。

配置場所の目安: `src/main/java/com/training/issuing/point/` のような新しいパッケージ。

## 制約
- ポイント付与率をJavaコード内に直接書いてはいけない（ハードコード禁止）。必ず`application.yml`から読み込むこと。
- `@ConfigurationProperties`を使うこと（`@Value`で個別に読み込む方法もあるが、今回はこちらを使う）。

## 設計上の論点

1. なぜポイント付与率をJavaコードに直接書かず、外部設定ファイルに置くのか？どんな場面でこれが役立つか？
2. `@Value("${issuing.point.rate}")`のようにフィールド単位で読み込む方法と、`@ConfigurationProperties`でクラスにまとめてバインドする方法、それぞれのメリット・デメリットは？
3. 「プロファイル」という仕組みが無かったら、開発環境と本番環境で異なる設定値を使い分けるのに、どんな方法が考えられますか？（環境変数、コマンドライン引数なども含めて考えてみてください）
4. ポイント付与率が「不正な値（例: マイナスの値）」だった場合、アプリ起動時に検知したいとしたら、どんな仕組みが使えると思いますか？

## 実装ヒント（答えにならない程度に）
- `@ConfigurationProperties`を使うクラスには`prefix`を指定します
- Spring Bootのバージョンによっては、`@ConfigurationProperties`を使うクラスを自動でBean登録するために、メインクラスや設定クラスに追加のアノテーション（`@EnableConfigurationProperties`など）が必要な場合があります
- プロファイルの切り替えは、`application.yml`内の指定、コマンドライン引数、環境変数など複数の方法があります。まずは`--spring.profiles.active=dev`のようなコマンドライン引数で試すのが分かりやすいです

## テスト観点
- `PointCalculator`に固定のポイント付与率を渡して、計算結果が正しいかを検証する単体テスト（Springを起動しない）
- 実際に`dev`/`prod`それぞれのプロファイルで起動し、意図した付与率が使われているかの動作確認

## レビュー観点
- ハードコードを避けられているか
- 設定クラスの命名・構造が分かりやすいか
- テストがSpringコンテナに依存しすぎていないか

## 学ぶべき設計原則
外部設定の分離（設定とロジックの分離）、環境ごとの設定切り替え、型安全な設定バインディング

---

## レビューログ

**実装**: ユーザーの依頼により、要件1〜5・追加のBean Validation対応まで含めてメンター（Claude）が直接実装。実装後、コードの意図をユーザーが1クラスずつ質問し（`IssuingApplication`、`PointRunner`、`PointProperties`）、その都度説明した上で設計論点を問答形式で確認した。

**実装物**:
- `application.yml`（デフォルト付与率1%）、`application-dev.yml`（5%）、`application-prod.yml`（1%）
- `PointProperties`: `@ConfigurationProperties(prefix = "issuing.point")`を付けたJavaの`record`。`@Validated`+`@DecimalMin`でマイナス値を起動時に検知するバリデーションも追加
- `PointCalculator`: `PointProperties`をコンストラクタインジェクションで受け取り、`BigDecimal`でポイント計算
- `PointRunner`: 動作確認用の`CommandLineRunner`
- `IssuingApplication`: `@EnableConfigurationProperties(PointProperties.class)`を追加
- `PointCalculatorTest`: Springを起動せず`PointProperties`を直接`new`して注入するテスト

**動作確認結果**:
| プロファイル/設定 | rate | 結果 |
|---|---|---|
| デフォルト | 0.01 | 10000円→100pt |
| dev | 0.05 | 10000円→500pt |
| prod | 0.01 | 10000円→100pt |
| 不正値(-0.01) | - | 起動失敗（バリデーションエラー） |

**設計論点の理解度**:
1. 外部設定にする理由について「環境ごとに変更したい値を管理しやすくなる」と回答。メンターから「再ビルド・再デプロイ不要になる」「非エンジニアでも値を変更できる」という補足を追加
2. `@Value` vs `@ConfigurationProperties`の違いについて、当初「メモリ消費量の差」という誤解をしていた。実際には両者ともSpringの`Environment`に設定値を保持する点で差はないことを説明して訂正。その後「グルーピングによる見通しの良さ」「キー名のタイプミスがコンパイル時に検知できない点は共通だが1箇所にまとめることでリスク低減」「テストのしやすさ（`PointProperties`を直接newできる）」を自分の言葉で正しく説明できた
3. プロファイルが無い場合の代替案として「環境ごとにアプリケーションを分ける」という極端な案を提示。メンターから環境変数・JVMシステムプロパティ・デプロイ時のファイル差し替えという、より現実的な代替案を補足
4. 不正な設定値の検知について、当初「値を使う側（`PointRunner`）でチェックする」という発想だった。「使う側が増えるたびにチェックが重複する」という問いかけから、「値が生成される場所（`PointProperties`自体）でチェックすべき」という考え方に気づけた。Bean Validation（`@Validated`+`@DecimalMin`）の存在を知らなかったため直接説明し、ユーザーの依頼によりメンターが実装。実際に不正値で起動が失敗することを実地で確認した

**つまずいた点**:
- `@Value`と`@ConfigurationProperties`の違いを「メモリ消費」という誤った軸で捉えていた（技術的な誤解のため直接訂正）
- Bean Validationの存在自体を知らなかった（前提知識のため直接説明）

**次回以降への種**:
- `PointRunner`は動作確認用の暫定的なクラスで、Level3でREST APIのコントローラーができたら役目を終える想定であることに言及済み

**フォローアップ質問（課題完了後）**:
- `@DecimalMin`の効果について質問があったため説明した。要点: Jakarta Bean Validation(JSR-380)のアノテーションで、数値が指定した最小値以上かを検証する。`value`は文字列指定（`BigDecimal`など任意精度の数値を誤差なく表現するため）。`inclusive`で境界値を含むかを制御。`@Validated`が付いたクラスにバインドされるタイミングで評価される。今回の「0以上」制約は`@PositiveOrZero`でも代替可能だが、`@DecimalMin`は任意の下限値を指定できる点でより柔軟、という比較にも触れた
