# Level7-1: Primitive Obsessionの解消 — `channel`のenum化

- 提示日: 2026-07-19
- 状態: 出題済み、提出待ち

## 学習テーマ
Fowlerのリファクタリング技法を、実際に動いている既存コードに適用する。今回は「テストという安全網を用意してから、小さなステップで書き換える」という進め方そのものを練習する。

## 背景
Level6-1のリファクタリング以降、`/code-review`で複数回「`channel`が生の`String`のまま比較されている（Primitive Obsession）」という指摘を受けてきました。今回はこれを正式に解消します。

## ゴール
- `channel`という生の`String`を、型安全な`Channel` enumに置き換える
- リファクタリングの前に、安全網となるテストを用意してから進める（Fowlerの基本原則）

## 対象コード
- `src/main/java/com/training/issuing/onboarding/OnboardRequest.java`
- `src/main/java/com/training/issuing/onboarding/ChannelBonusPolicy.java`
- `src/main/java/com/training/issuing/onboarding/WebChannelBonusPolicy.java` / `AppChannelBonusPolicy.java` / `StoreChannelBonusPolicy.java`
- `src/main/java/com/training/issuing/onboarding/ChannelBonusCalculator.java`
- `src/main/java/com/training/issuing/onboarding/MemberOnboardingService.java`

## 要件

1. **リファクタリング前に、`ChannelBonusCalculator`の単体テストを追加する**（現状テストが存在しない）。少なくとも、WEB/APP/STOREそれぞれで正しいポイント数が返ること、未知のチャネルで`0`が返ることを検証する。
2. `Channel`という`enum`（`WEB`, `APP`, `STORE`）を作る。
3. `ChannelBonusPolicy`インターフェースと、その実装クラス（Web/App/Store）を、`String`ではなく`Channel`を受け取る形に変更する。
4. `OnboardRequest`の`channel`フィールドの型をどうするか（`String`のままにするか、`Channel`にするか）を、設計上の論点を踏まえて決め、実装する。
5. 一気にすべて書き換えるのではなく、**都度コンパイルが通り、テストが通る単位で**、小さなステップで進める。

## 制約
- 手順1（安全網となるテストの追加）を必ず最初に行うこと。テストがない状態でリファクタリングを始めない。
- 各ステップの後に必ず`./gradlew test`を実行し、グリーンであることを確認してから次のステップに進む。

## 設計上の論点

1. `OnboardRequest`（DTO）が受け取る`channel`は、そもそも`String`のままであるべきか、`Channel` enumにすべきか？外部から渡ってくるのは結局のところ文字列（JSON）である、という点を踏まえて考えてください。
2. 不正な文字列（例: `"FOO"`）が渡された場合、DTOの層でエラーにすべきか、それとも`Channel`への変換時にエラーにすべきか？それぞれの場合、エラーメッセージの分かりやすさはどう変わりますか？
3. Jacksonは`enum`をJSONの文字列と自動的に相互変換してくれます。この仕組みを使う場合と、自分で変換ロジックを書く場合で、どちらが良いと思いますか？

## 実装ヒント（答えにならない程度に）
- Fowlerの「小さなステップでリファクタリングする」という考え方は、「一度にたくさん変更して、最後にまとめてテストする」のではなく、「1つ変更したら、すぐテストを実行して確認する」という進め方です。コンパイルエラーやテスト失敗が起きたときに、どこが原因か特定しやすくなります
- `enum`はJavaの言語機能として、`switch`文で網羅性チェック（`default`が無くても全パターンを書けば警告が出ない、など）と相性が良い場合があります
- Bean Validationの`@NotBlank`は`String`にしか使えません。`enum`フィールドに対してバリデーションをかけたい場合、別のアプローチが必要になります

## テスト観点
- 手順1で追加した`ChannelBonusCalculator`のテストが、リファクタリング後も変わらず通ること（テストコード自体は書き直さなくてよいはず、というのがこのリファクタリングの目標です）
- 不正なチャネル文字列を渡した場合の挙動（エラーになるか、`0`ポイントとして扱われるか）

## レビュー観点
- テストという安全網を用意してから進められているか
- 小さなステップで進め、都度動作確認できているか
- `Channel`型の導入により、`if/else`や文字列比較が実際に減っているか
- DTOの型設計（`String` vs `Channel`）の判断に、理由を説明できるか

## 学ぶべき設計原則
Primitive Obsessionの解消、Fowlerの安全なリファクタリングの進め方（テスト→小さな変更→確認の繰り返し）、型安全性

---

## レビューログ

（提出・レビュー後にここへ追記する：学んだこと・つまずいた点・模範解答の要旨）
