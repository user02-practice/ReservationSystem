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

}