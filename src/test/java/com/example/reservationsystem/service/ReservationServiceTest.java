package com.example.reservationsystem.service;

import com.example.reservationsystem.entity.Reservation;
import com.example.reservationsystem.repository.ReservationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

// ReservationServiceの統合検索をテストする
@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    // 実際のDBには接続せず、Repositoryの代わりとなるMockを使用する
    @Mock
    private ReservationRepository reservationRepository;

    // MockのRepositoryをReservationServiceへ渡す
    @InjectMocks
    private ReservationService reservationService;


    // 氏名による部分一致検索を確認する
    @Test
    void searchReservations_氏名で検索できる() {

        Reservation yamada = createReservation(
                "山田太郎",
                LocalDate.of(2026, 9, 25)
        );

        Reservation suzuki = createReservation(
                "鈴木一郎",
                LocalDate.of(2026, 9, 30)
        );

        // 近い順で取得した予約一覧をMockで用意する
        when(reservationRepository.findAllByOrderByPreferredDateAsc())
                .thenReturn(List.of(yamada, suzuki));

        // 「山田」で検索する
        List<Reservation> result =
                reservationService.searchReservations(
                        "山田",
                        null,
                        "asc"
                );

        // 山田太郎だけが取得されることを確認する
        assertEquals(1, result.size());
        assertEquals("山田太郎", result.get(0).getCustomerName());
    }


    // 予約希望日の完全一致検索を確認する
    @Test
    void searchReservations_予約希望日で検索できる() {

        Reservation yamada = createReservation(
                "山田太郎",
                LocalDate.of(2026, 9, 25)
        );

        Reservation suzuki = createReservation(
                "鈴木一郎",
                LocalDate.of(2026, 9, 30)
        );

        when(reservationRepository.findAllByOrderByPreferredDateAsc())
                .thenReturn(List.of(yamada, suzuki));

        // 2026-09-30で検索する
        List<Reservation> result =
                reservationService.searchReservations(
                        null,
                        LocalDate.of(2026, 9, 30),
                        "asc"
                );

        // 9月30日の予約だけ取得されることを確認する
        assertEquals(1, result.size());
        assertEquals("鈴木一郎", result.get(0).getCustomerName());
        assertEquals(
                LocalDate.of(2026, 9, 30),
                result.get(0).getPreferredDate()
        );
    }


    // 遠い順が指定された場合の取得を確認する
    @Test
    void searchReservations_遠い順で取得できる() {

        Reservation suzuki = createReservation(
                "鈴木一郎",
                LocalDate.of(2026, 10, 10)
        );

        Reservation yamada = createReservation(
                "山田太郎",
                LocalDate.of(2026, 9, 25)
        );

        // Repositoryが遠い順で返すデータを用意する
        when(reservationRepository.findAllByOrderByPreferredDateDesc())
                .thenReturn(List.of(suzuki, yamada));

        List<Reservation> result =
                reservationService.searchReservations(
                        null,
                        null,
                        "desc"
                );

        assertEquals(2, result.size());

        // 最初に遠い日付が来ることを確認する
        assertEquals(
                LocalDate.of(2026, 10, 10),
                result.get(0).getPreferredDate()
        );

        assertEquals(
                LocalDate.of(2026, 9, 25),
                result.get(1).getPreferredDate()
        );
    }


    // 氏名と予約希望日を同時に指定した場合を確認する
    @Test
    void searchReservations_氏名と予約希望日を組み合わせて検索できる() {

        Reservation yamada1 = createReservation(
                "山田太郎",
                LocalDate.of(2026, 9, 25)
        );

        Reservation yamada2 = createReservation(
                "山田花子",
                LocalDate.of(2026, 9, 30)
        );

        Reservation suzuki = createReservation(
                "鈴木一郎",
                LocalDate.of(2026, 9, 30)
        );

        when(reservationRepository.findAllByOrderByPreferredDateAsc())
                .thenReturn(List.of(yamada1, yamada2, suzuki));

        // 「山田」かつ「2026-09-30」で検索する
        List<Reservation> result =
                reservationService.searchReservations(
                        "山田",
                        LocalDate.of(2026, 9, 30),
                        "asc"
                );

        // 山田花子だけになることを確認する
        assertEquals(1, result.size());
        assertEquals("山田花子", result.get(0).getCustomerName());
        assertEquals(
                LocalDate.of(2026, 9, 30),
                result.get(0).getPreferredDate()
        );
    }


    // テスト用Reservationを簡単に作成するためのメソッド
    private Reservation createReservation(
            String customerName,
            LocalDate preferredDate) {

        Reservation reservation = new Reservation();

        reservation.setCustomerName(customerName);
        reservation.setPreferredDate(preferredDate);

        return reservation;
    }
}