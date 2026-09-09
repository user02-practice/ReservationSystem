package com.example.reservationsystem.entity;

// JPAでEntityを扱うために使用する
import jakarta.persistence.*;

// 入力値が空欄ではないことをチェックするために使用する
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;

// 日付を扱うために使用する
import java.time.LocalDate;

// ReservationクラスをEntity（データベースに保存するデータ）として指定する
@Entity

// データベースでは「reservations」という名前のテーブルを使用する
@Table(name = "reservations")
public class Reservation {

    // 予約情報を一意に識別するためのID
    // IDはデータベース側で自動的に採番する
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 予約者の氏名
    // 空欄での登録を禁止する
    @NotBlank(message = "氏名を入力してください")
    private String customerName;

    // 予約人数
    // 1人以上であることをチェックする
    @NotNull(message = "人数を入力してください")
    @Min(value = 1, message = "人数は1人以上を入力してください")
    private Integer numberOfPeople;

    // 予約希望日
// 未入力を禁止する
// HTMLの日付入力欄で扱える yyyy-MM-dd 形式として指定する
    @NotNull(message = "予約希望日を入力してください")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate preferredDate;

    // 予約者の電話番号
    // 電話番号は必須入力とする
    // 海外の電話番号にも対応するため、形式については厳密なチェックを行わない
    @NotBlank(message = "電話番号を入力してください")
    private String phoneNumber;

    // 予約者のメールアドレス
    // 必須入力とし、メールアドレスの形式もチェックする
    @NotBlank(message = "メールアドレスを入力してください")
    @Email(message = "メールアドレスの形式が正しくありません")
    private String email;

    // 予約に関する備考
    // 入力は任意だが、入力する場合は100文字以内とする
    @Size(max = 100, message = "備考は100文字以内で入力してください")
    private String remarks;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public Integer getNumberOfPeople() {
        return numberOfPeople;
    }

    public void setNumberOfPeople(Integer numberOfPeople) {
        this.numberOfPeople = numberOfPeople;
    }

    public LocalDate getPreferredDate() {
        return preferredDate;
    }

    public void setPreferredDate(LocalDate preferredDate) {
        this.preferredDate = preferredDate;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}