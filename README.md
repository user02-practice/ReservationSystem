# 予約管理システム

Spring Bootを使用して作成した予約管理システムです。

利用者がWeb画面から予約を登録し、管理者が予約情報の確認・検索・編集・削除を行うことができます。
また、予約完了時には登録されたメールアドレス宛に予約受付メールを自動送信します。

## 主な機能

### 利用者向け
- 予約の新規登録
- 入力内容のバリデーション
- 予約完了画面の表示
- 予約完了時の確認メール送信

### 管理者向け
- 管理者ログイン
- 予約一覧表示
- 氏名による部分一致検索
- 予約希望日による検索
- 予約受付順での表示
- 予約希望日の近い順・遠い順での並び替え
- 予約詳細の確認
- 予約内容の編集
- 予約の削除

## 使用技術

- Java
- Spring Boot
- Spring MVC
- Spring Data JPA
- Spring Security
- Thymeleaf
- H2 Database
- JavaMailSender
- Gmail SMTP
- HTML / CSS
- Maven

## テスト

Mavenを使用してテストを実行できます。

```powershell
.\mvnw test
```

現在、9件のテストを作成しており、すべて成功しています。


## 環境変数

予約確認メールの送信にはGmail SMTPを使用しています。

メールアドレスやアプリパスワードなどの認証情報をソースコードに直接記述しないよう、以下の環境変数を使用しています。

| 環境変数 | 内容 |
| --- | --- |
| `MAIL_USERNAME` | メール送信に使用するGmailアドレス |
| `MAIL_PASSWORD` | Gmailのアプリパスワード |

`application.properties` では以下のように環境変数を参照しています。

```properties
spring.mail.username=${MAIL_USERNAME}
spring.mail.password=${MAIL_PASSWORD}
```

認証情報そのものはGitHubには保存しません。


## 画面URL

アプリケーションを起動した状態で、以下のURLから各画面にアクセスできます。

### 利用者向け

予約登録画面

```text
http://localhost:8080/reservations/new
```

利用者は氏名、人数、予約希望日、電話番号、メールアドレス、備考を入力して予約を登録できます。

予約が正常に登録されると予約完了画面が表示され、入力したメールアドレス宛に予約受付メールが送信されます。

### 管理者向け

予約管理画面

```text
http://localhost:8080/admin/reservations
```

管理者向け画面へのアクセスにはログインが必要です。

ログイン後は、予約一覧の確認、氏名・予約希望日による検索、並び替え、詳細確認、編集、削除を行うことができます。

並び順は以下の3種類から選択できます。

- 予約受付順
- 予約希望日の近い順
- 予約希望日の遠い順

## 起動方法

### 1. 環境変数を設定する

予約確認メールを送信するため、以下の環境変数を設定します。

```text
MAIL_USERNAME
MAIL_PASSWORD
```

`MAIL_USERNAME` にはメール送信に使用するGmailアドレス、`MAIL_PASSWORD` にはGmailのアプリパスワードを設定します。

### 2. アプリケーションを起動する

IntelliJ IDEAから `ReservationSystemApplication` を実行します。

または、プロジェクトのルートディレクトリから以下のコマンドで起動できます。

```powershell
.\mvnw spring-boot:run
```

### 3. 予約を登録する

ブラウザから以下の予約登録画面にアクセスします。

```text
http://localhost:8080/reservations/new
```

必要事項を入力して予約を登録します。

### 4. 予約を管理する

以下の管理者向け画面にアクセスします。

```text
http://localhost:8080/admin/reservations
```

ログイン後、登録された予約の検索・確認・編集・削除などを行うことができます。