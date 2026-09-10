package com.example.reservationsystem.controller;

// Reservation Entityを使用するためにインポートする
import com.example.reservationsystem.entity.Reservation;

// GETリクエストを受け取るために使用する
import com.example.reservationsystem.service.MailService;
import org.springframework.web.bind.annotation.GetMapping;

// HTMLへデータを渡すために使用する
import org.springframework.ui.Model;

// ReservationServiceを使用するためにインポートする
import com.example.reservationsystem.service.ReservationService;

// Spring MVCのControllerとして認識させるために使用する
import org.springframework.stereotype.Controller;

import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

// 予約画面からのリクエストを受け取るController
@Controller
public class ReservationController {

    // 予約に関する処理を行うService
    private final ReservationService reservationService;

    // 予約確認メールを送信するService
    private final MailService mailService;

    // ReservationServiceを受け取るコンストラクタ
    public ReservationController(
            ReservationService reservationService,
            MailService mailService) {

        this.reservationService = reservationService;
        this.mailService = mailService;
    }

    // 予約登録画面を表示する
    @GetMapping("/reservations/new")
    public String showReservationForm(Model model) {

        // 空のReservationオブジェクトを作成し、予約入力フォームで使用できるようにする
        model.addAttribute("reservation", new Reservation());

        // templates/reservations/form.html を表示する
        return "reservations/form";
    }

    // 予約フォームから送信された内容を受け取り、予約を登録する
    @PostMapping("/reservations")
    public String createReservation(
            @Valid Reservation reservation,
            BindingResult bindingResult,
            Model model) {

        // 入力内容にエラーがある場合は、予約登録画面を再表示する
        if (bindingResult.hasErrors()) {
            return "reservations/form";
        }

        // 入力内容に問題がなければ、予約情報をデータベースに保存する
        Reservation savedReservation =
                reservationService.saveReservation(reservation);

        // 予約者へ予約確認メールを送信する
        mailService.sendReservationConfirmation(
                savedReservation.getEmail(),
                savedReservation.getCustomerName(),
                savedReservation.getPreferredDate().toString(),
                savedReservation.getNumberOfPeople()
        );

        // 予約完了画面で表示するために、保存済みの予約情報を渡す
        model.addAttribute("reservation", savedReservation);

        // 予約完了画面を表示する
        return "reservations/complete";
    }

    // 予約一覧を表示する
// 氏名・予約希望日・並び順を同時に受け取り、検索条件を保持する
    @GetMapping("/admin/reservations")
    public String showReservationList(
            @RequestParam(required = false) String customerName,
            @RequestParam(required = false) String preferredDate,
            @RequestParam(defaultValue = "asc") String order,
            Model model) {

        // 予約希望日を格納する変数
        LocalDate date = null;

        // 予約希望日が入力されている場合のみLocalDateへ変換する
        if (preferredDate != null && !preferredDate.isBlank()) {
            date = LocalDate.parse(preferredDate);
        }

        // 氏名・予約希望日・並び順を指定して予約情報を取得する
        List<Reservation> reservations =
                reservationService.searchReservations(
                        customerName,
                        date,
                        order
                );

        // 検索結果を一覧画面へ渡す
        model.addAttribute("reservations", reservations);

        // 検索後も入力した条件を画面に残す
        model.addAttribute("customerName", customerName);
        model.addAttribute("preferredDate", preferredDate);
        model.addAttribute("order", order);

        // 予約一覧画面を表示する
        return "reservations/list";
    }

    // 予約詳細画面を表示する
    @GetMapping("/admin/reservations/{id}")
    public String showReservationDetail(
            @PathVariable Long id,
            Model model) {

        // 予約IDを使って、該当する予約情報を1件取得する
        Reservation reservation = reservationService.getReservationById(id);

        // 該当する予約が見つからない場合は予約一覧画面へ戻る
        if (reservation == null) {
            return "redirect:/admin/reservations";
        }

        // 取得した予約情報を詳細画面で使用できるようにModelへ渡す
        model.addAttribute("reservation", reservation);

        // 予約詳細画面を表示する
        return "reservations/detail";
    }

    // 予約編集画面を表示する
    @GetMapping("/admin/reservations/{id}/edit")
    public String showEditForm(
            @PathVariable Long id,
            Model model) {

        // 予約IDを使って、編集対象の予約情報を1件取得する
        Reservation reservation = reservationService.getReservationById(id);

        // 該当する予約が見つからない場合は、予約一覧画面へ戻る
        if (reservation == null) {
            return "redirect:/admin/reservations";
        }

        // 編集画面で現在の予約情報を使用できるようにModelへ渡す
        model.addAttribute("reservation", reservation);

        // 予約編集画面を表示する
        return "reservations/edit";
    }

    // 編集フォームから送信された予約情報を更新する
    @PostMapping("/admin/reservations/{id}")
    public String updateReservation(
            @PathVariable Long id,
            @Valid Reservation reservation,
            BindingResult bindingResult,
            Model model) {

        // 入力内容にエラーがある場合は、編集画面を再表示する
        if (bindingResult.hasErrors()) {

            // 編集対象のIDを保持する
            reservation.setId(id);

            // 入力内容を編集画面で再表示できるようにする
            model.addAttribute("reservation", reservation);

            return "reservations/edit";
        }

        // URLから受け取ったIDをReservationに設定する
        reservation.setId(id);

        // IDが設定されたReservationを保存することで、
        // 新規登録ではなく既存の予約情報を更新する
        reservationService.saveReservation(reservation);

        // 更新後は予約詳細画面へ移動する
        return "redirect:/admin/reservations/" + id;
    }

    // 指定した予約IDの予約情報を削除する
    @PostMapping("/admin/reservations/{id}/delete")
    public String deleteReservation(@PathVariable Long id) {

        // 予約IDを使って、該当する予約情報を削除する
        reservationService.deleteReservation(id);

        // 削除後は予約一覧画面へ戻る
        return "redirect:/admin/reservations";
    }

}