package com.example.reservationsystem.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

// メール送信処理を担当するServiceクラス
@Service
public class MailService {

    // Spring Bootのメール送信機能
    private final JavaMailSender mailSender;

    // JavaMailSenderを受け取るコンストラクタ
    public MailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    // 指定したメールアドレスへ予約確認メールを送信する
    public void sendReservationConfirmation(
            String to,
            String customerName,
            String preferredDate,
            Integer numberOfPeople) {

        // 送信するメールを作成する
        SimpleMailMessage message = new SimpleMailMessage();

        // 宛先
        message.setTo(to);

        // 件名
        message.setSubject("ご予約を受け付けました");

        // 本文
        message.setText(
                customerName + " 様\n\n" +
                        "ご予約ありがとうございます。\n\n" +
                        "予約希望日：" + preferredDate + "\n" +
                        "人数：" + numberOfPeople + "名\n\n" +
                        "ご予約を受け付けました。"
        );

        // メールを送信する
        mailSender.send(message);
    }
}