package com.example.reservationsystem.controller;

// Reservation Entityを使用するためにインポートする
import com.example.reservationsystem.entity.Reservation;

// GETリクエストを受け取るために使用する
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

    // ReservationServiceを受け取るコンストラクタ
    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
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

        // 予約完了画面で表示するために、保存済みの予約情報を渡す
        model.addAttribute("reservation", savedReservation);

        // 予約完了画面を表示する
        return "reservations/complete";
    }

    // 予約一覧画面を表示する
    @GetMapping("/reservations")
    public String showReservationList(Model model) {

        // データベースに保存されている予約情報をすべて取得する
        List<Reservation> reservations = reservationService.getAllReservations();

        // 取得した予約情報を予約一覧画面で使用できるようにModelへ渡す
        model.addAttribute("reservations", reservations);

        // 予約一覧画面を表示する
        return "reservations/list";
    }

    // 予約詳細画面を表示する
    @GetMapping("/reservations/{id}")
    public String showReservationDetail(
            @PathVariable Long id,
            Model model) {

        // 予約IDを使って、該当する予約情報を1件取得する
        Reservation reservation = reservationService.getReservationById(id);

        // 該当する予約が見つからない場合は予約一覧画面へ戻る
        if (reservation == null) {
            return "redirect:/reservations";
        }

        // 取得した予約情報を詳細画面で使用できるようにModelへ渡す
        model.addAttribute("reservation", reservation);

        // 予約詳細画面を表示する
        return "reservations/detail";
    }

    // 予約編集画面を表示する
    @GetMapping("/reservations/{id}/edit")
    public String showEditForm(
            @PathVariable Long id,
            Model model) {

        // 予約IDを使って、編集対象の予約情報を1件取得する
        Reservation reservation = reservationService.getReservationById(id);

        // 該当する予約が見つからない場合は、予約一覧画面へ戻る
        if (reservation == null) {
            return "redirect:/reservations";
        }

        // 編集画面で現在の予約情報を使用できるようにModelへ渡す
        model.addAttribute("reservation", reservation);

        // 予約編集画面を表示する
        return "reservations/edit";
    }

    // 編集フォームから送信された予約情報を更新する
    @PostMapping("/reservations/{id}")
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
        return "redirect:/reservations/" + id;
    }

    // 指定した予約IDの予約情報を削除する
    @PostMapping("/reservations/{id}/delete")
    public String deleteReservation(@PathVariable Long id) {

        // 予約IDを使って、該当する予約情報を削除する
        reservationService.deleteReservation(id);

        // 削除後は予約一覧画面へ戻る
        return "redirect:/reservations";
    }

    // 氏名の一部を指定して予約情報を検索する
    @GetMapping("/reservations/search")
    public String searchReservationsByCustomerName(
            @RequestParam String customerName,
            Model model) {

        // 氏名の部分一致検索を行う
        List<Reservation> reservations =
                reservationService.searchByCustomerName(customerName);

        // 検索結果を予約一覧画面へ渡す
        model.addAttribute("reservations", reservations);

        // 入力した検索文字も画面に戻す
        model.addAttribute("customerName", customerName);

        // 予約一覧画面を表示する
        return "reservations/list";
    }

    // 予約希望日の並び順を指定して予約一覧を表示する
    @GetMapping("/reservations/sort")
    public String sortReservations(
            @RequestParam String order,
            Model model) {

        List<Reservation> reservations;

        // orderの値によって並び順を切り替える
        if ("desc".equals(order)) {

            // 予約希望日の遠い順で取得する
            reservations =
                    reservationService.getReservationsByPreferredDateDesc();

        } else {

            // 予約希望日の近い順で取得する
            reservations =
                    reservationService.getReservationsByPreferredDateAsc();
        }

        // 並び替えた予約一覧を画面へ渡す
        model.addAttribute("reservations", reservations);

        // 現在選択している並び順も画面へ渡す
        model.addAttribute("order", order);

        // 予約一覧画面を表示する
        return "reservations/list";
    }

    // 予約希望日を指定して予約情報を検索する
    @GetMapping("/reservations/date-search")
    public String searchReservationsByPreferredDate(
            @RequestParam(required = false) String preferredDate,
            Model model) {

        // 予約希望日が入力されていない場合は、予約一覧画面へ戻る
        if (preferredDate == null || preferredDate.isBlank()) {
            return "redirect:/reservations";
        }

        // 文字列で受け取った日付をLocalDateに変換する
        LocalDate date = LocalDate.parse(preferredDate);

        // 指定した予約希望日の予約情報を検索する
        List<Reservation> reservations =
                reservationService.searchByPreferredDate(date);

        // 検索結果を予約一覧画面へ渡す
        model.addAttribute("reservations", reservations);

        // 検索した予約希望日を画面へ戻す
        model.addAttribute("preferredDate", date);

        // 予約一覧画面を表示する
        return "reservations/list";
    }
}