# 環境構築ガイド（VSCode + macOS / Homebrew）

このトレーニングを進めるための開発環境セットアップ手順です。
Level1〜3くらいまでは「JDKとVSCode」だけで進められます。Docker/PostgreSQLはLevel4（データベース）から必要になります。すべてを最初に揃える必要はありません。

## 現在の環境チェック結果（2026-07-04時点）

| 項目 | 状態 |
|---|---|
| Java | sdkman経由でJava 21 (Temurin) 導入済み・有効化確認済み（2026-07-05） |
| VSCode本体 | インストール済み（`/Applications/Visual Studio Code.app`） |
| `code` CLIコマンド | 未設定（拡張機能はVSCode GUIの拡張機能タブから導入すれば代替可） |
| Gradle（グローバル） | 未インストール（Gradle Wrapperを使うため基本的に不要） |
| Docker | 未インストール（Level4以降で必要） |
| Git | インストール済み |

---

## 1. JDK 21 を入れる

複数バージョンのJDKを切り替えられるようにしておくと実務でも便利です。`sdkman` を使う方法を推奨します。

```bash
# sdkmanが未導入なら
curl -s "https://get.sdkman.io" | bash
```

上記のインストーラーを実行すると、`~/.zshrc` に以下の行が自動的に追記されます（手動で追記する必要はありません）。

```bash
source "$HOME/.sdkman/bin/sdkman-init.sh"
```

追記後は一度ターミナルを再起動するか、以下を実行して今のセッションにも反映させます。

```bash
source "$HOME/.zshrc"

# Java 21 (Temurin) を導入
sdk install java 21.0.5-tem

# プロジェクトディレクトリでのデフォルトに設定
sdk use java 21.0.5-tem
sdk default java 21.0.5-tem  # ← グローバルデフォルトにする場合。既存のJava25運用と共存させたいなら省略してもよい

java -version   # 21系になっていることを確認
```

**論点**: グローバルのJavaバージョンを21に切り替えるか、プロジェクト単位で `sdk use` を都度実行するか、どちらが実務的に安全だと思いますか？（他のプロジェクトへの影響を考えてみてください）

Homebrewで直接入れる場合:
```bash
brew install openjdk@21
```
（この場合はPATH設定が別途必要になります。sdkmanの方がバージョン切り替えが楽なため推奨）

---

## 2. VSCode拡張機能

以下をVSCodeの拡張機能タブ、または以下のコマンドで導入します。

```bash
# まずVSCodeの `code` コマンドをPATHに通す
# VSCodeを起動 → Cmd+Shift+P → "Shell Command: Install 'code' command in PATH" を実行

# その後、以下でまとめて拡張機能を導入
code --install-extension vscjava.vscode-java-pack        # Extension Pack for Java（デバッガ・テスト・Maven等一式）
code --install-extension vmware.vscode-spring-boot        # Spring Boot向け言語サポート
code --install-extension vscjava.vscode-spring-initializr # Spring Initializrからプロジェクト生成
code --install-extension vscjava.vscode-spring-boot-dashboard # Boot実行管理ダッシュボード
code --install-extension vscjava.vscode-gradle             # Gradleタスク実行
code --install-extension redhat.vscode-yaml                # application.yml編集
```

Docker関連（Level4以降で使うので今すぐでなくてよい）:
```bash
code --install-extension ms-azuretools.vscode-docker
```

**Mermaid図（クラス図など）を見る場合の注意（2026-07-12確認）**: VSCode 1.90以降（このマシンでは1.127.0）は、Markdownプレビューに**Mermaid描画機能が標準搭載**されている。そのため`bierner.markdown-mermaid`のようなサードパーティ拡張機能を追加でインストールすると、設定項目の登録が衝突し（`Cannot register '...'. This property is already registered.`という警告が出る）、**プレビューの枠だけ表示されて中身が描画されない**という不具合が起きた。Mermaid拡張機能は追加インストールせず、VSCode標準機能のみで表示すること。既にインストールしてしまった場合はアンインストール＋ウィンドウ再読み込みで解消する。

---

## 3. Docker（Level4以降で必要）

PostgreSQLとTestcontainersのために必要です。Level1〜3の間は未導入でも進められます。

```bash
brew install --cask docker
```

インストール後、Docker Desktopアプリを一度起動してデーモンを立ち上げておいてください。

**状況（2026-07-06確認済み）**: Docker Desktopインストール・起動確認済み。`docker compose`（v5.1.4、Docker Desktop同梱のCLIプラグイン）が使用可能。Docker Desktopアプリを起動していない状態だと`docker info`が「デーモンに接続できない」旨のエラーになるので、作業前に起動しておくこと。

**重要（ポート衝突の既知の問題、2026-07-12確認）**: このマシンにはDocker以外に、ローカルにインストールされた別のPostgreSQLプロセスが`127.0.0.1:5432`ですでにリッスンしている。`docker-compose.yml`でPostgreSQLコンテナのホスト側ポートを標準の`5432`のままにすると、`localhost:5432`への接続が意図せずそちらのローカルプロセスにルーティングされ、「role "xxx" does not exist」のような紛らわしいエラーになる。本プロジェクトの`docker-compose.yml`ではホスト側ポートを**`5433`**に変更して回避している（`application.yml`の`spring.datasource.url`も`jdbc:postgresql://localhost:5433/issuing`に合わせてあるので、変更しないこと）。

**重要（Testcontainers × 新しいDocker Desktopのバージョン不整合、2026-07-18確認）**: このマシンのDocker Desktop（4.80.0、Engine 29.6.1、`MinAPIVersion` 1.40）は、Docker API v1.40未満のリクエストを`Status 400`で拒否する。ところがTestcontainers（内部で使う`docker-java`ライブラリ）は、画像の存在確認（`InspectImageCmdExec`）などの一部の内部処理で、古いAPIバージョン（v1.32）を使おうとするため、`client version 1.32 is too old`という紛らわしいエラーで**Testcontainersのコンテナ起動そのものが失敗する**。

- 症状: `curl --unix-socket /var/run/docker.sock ...`では正常に応答が返るのに、Testcontainers経由だと`Could not find a valid Docker environment`や`BadRequestException`が出る
- 原因の切り分け方: `curl --unix-socket /var/run/docker.sock http://localhost/v1.24/version`のように、意図的に古いAPIバージョンを指定したパスにアクセスして`400`が返るか確認する
- 対応: `build.gradle`の`test`タスクに`systemProperty "api.version", "1.41"`を追加し、`docker-java`に新しいAPIバージョンを明示的に使わせることで解消した（Testcontainersのバージョンを最新（1.21.3）にしても、これ単体では解消しなかった。あわせて必要）
- Testcontainersのバージョンアップ（この対応をしなくても済むようになる可能性がある）で将来的に不要になるかもしれないが、現時点ではこの設定が必須

---

## 4. プロジェクトの始め方

Level1の時点で、JUnit5でテストを実行するために軽量なGradleプロジェクト（Spring Bootなし）を作成済みです（2026-07-05）。

- ビルドツール: Gradle（Groovy DSL, `build.gradle`）
- Wrapper生成済み（`./gradlew`）
- ソース: `src/main/java/com/training/issuing/`
- テスト: `src/test/java/com/training/issuing/`
- 動作確認コマンド: `./gradlew build`, `./gradlew test`, `./gradlew bootRun`

**重要**: `gradle.properties`に`org.gradle.java.home=/Users/toshihiro/.sdkman/candidates/java/21.0.5-tem`を設定済み（2026-07-05）。
理由: GradleデーモンをHomebrewのJava25で動かすと、Spring Bootのgradleプラグイン読み込み時に`Unsupported class file major version 69`エラーが発生したため。Gradle自体をJava21(sdkman管理のTemurin)で動かすことで解消した（`build.gradle`の`toolchain`指定はコンパイル対象のJavaバージョンを指定するもので、Gradle自体の実行バイナリには影響しないため、これとは別に対応が必要だった）。

Level2でSpring Bootの依存関係（`spring-boot-starter`, `spring-boot`/`io.spring.dependency-management`プラグイン）を追加済み。エントリーポイントは`src/main/java/com/training/issuing/IssuingApplication.java`。DB/Web系の依存関係（Spring Web, Spring Data JPA, Validation, PostgreSQL Driver, Flyway）は該当Levelに進むタイミングで追加していく。

---

## 5. 動作確認チェックリスト

- [ ] `java -version` が `21.x` と表示される
- [ ] VSCodeで `.java` ファイルを開くと補完・エラー表示が効く（Extension Pack for Java導入後）
- [ ] （Level4以降）`docker --version` が表示され、Docker Desktopが起動できる

環境構築で詰まった場合は、エラーメッセージを共有してください。一緒に切り分けます。
