package com.example.reservationsystem.service;

// Reservation Entityを使用するためにインポートする
import com.example.reservationsystem.entity.Reservation;

// ReservationRepositoryを使用するためにインポートする
import com.example.reservationsystem.repository.ReservationRepository;

// SpringにServiceクラスとして認識させるために使用する
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

// 予約に関する処理を担当するServiceクラス
@Service
public class ReservationService {

    // データベース操作を行うRepository
    private final ReservationRepository reservationRepository;

    // ReservationRepositoryを受け取るコンストラクタ
    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    // 予約情報をデータベースに保存する
    public Reservation saveReservation(Reservation reservation) {
        return reservationRepository.save(reservation);
    }

    // 登録されている予約情報をすべて取得する
    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    // 予約IDを指定して、該当する予約情報を1件取得する
    public Reservation getReservationById(Long id) {
        return reservationRepository.findById(id)
                .orElse(null);
    }

    // 予約IDを指定して、該当する予約情報を削除する
    public void deleteReservation(Long id) {
        reservationRepository.deleteById(id);
    }

    // 氏名の一部を指定して、該当する予約情報を検索する
    public List<Reservation> searchByCustomerName(String customerName) {
        return reservationRepository.findByCustomerNameContaining(customerName);
    }

    // 予約希望日を指定して、該当する予約情報を検索する
    public List<Reservation> searchByPreferredDate(LocalDate preferredDate) {
        return reservationRepository.findByPreferredDate(preferredDate);
    }

    // 予約希望日が近い順で予約情報を取得する
    public List<Reservation> getReservationsByPreferredDateAsc() {
        return reservationRepository.findAllByOrderByPreferredDateAsc();
    }

    // 予約希望日が遠い順で予約情報を取得する
    public List<Reservation> getReservationsByPreferredDateDesc() {
        return reservationRepository.findAllByOrderByPreferredDateDesc();
    }

    // 氏名・予約希望日・並び順を組み合わせて予約情報を検索する
    public List<Reservation> searchReservations(
            String customerName,
            LocalDate preferredDate,
            String order) {

        List<Reservation> reservations;

        // まず並び順に応じて全予約を取得する
        if ("desc".equals(order)) {

            // 予約希望日の遠い順で取得する
            reservations =
                    reservationRepository.findAllByOrderByPreferredDateDesc();

        } else {

            // 予約希望日の近い順で取得する
            reservations =
                    reservationRepository.findAllByOrderByPreferredDateAsc();
        }

        // 氏名が入力されている場合は部分一致で絞り込む
        if (customerName != null && !customerName.isBlank()) {
            reservations = reservations.stream()
                    .filter(reservation ->
                            reservation.getCustomerName().contains(customerName))
                    .toList();
        }

        // 予約希望日が指定されている場合は一致する予約だけに絞り込む
        if (preferredDate != null) {
            reservations = reservations.stream()
                    .filter(reservation ->
                            preferredDate.equals(reservation.getPreferredDate()))
                    .toList();
        }

        return reservations;
    }
}