package com.example.reservationsystem.controller;

import com.example.reservationsystem.entity.Reservation;
import com.example.reservationsystem.service.ReservationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// ReservationControllerの統合検索をテストする
@ExtendWith(MockitoExtension.class)
class ReservationControllerTest {

    private MockMvc mockMvc;

    // 実際のServiceの代わりにMockを使用する
    @Mock
    private ReservationService reservationService;

    // MockのServiceをControllerへ渡す
    @InjectMocks
    private ReservationController reservationController;


    @BeforeEach
    void setUp() {

        // Controllerだけを対象としてHTTPリクエストのテスト環境を作る
        mockMvc = MockMvcBuilders
                .standaloneSetup(reservationController)
                .build();
    }


    // 氏名をControllerが受け取れることを確認する
    @Test
    void showReservationList_氏名検索条件を受け取れる() throws Exception {

        when(reservationService.searchReservations(
                "山田",
                null,
                "asc"
        )).thenReturn(List.of());

        mockMvc.perform(
                        get("/reservations")
                                .param("customerName", "山田")
                                .param("order", "asc")
                )
                .andExpect(status().isOk())
                .andExpect(view().name("reservations/list"))

                // 検索後も「山田」がModelに保持されていることを確認
                .andExpect(model().attribute("customerName", "山田"))

                // 並び順が保持されていることを確認
                .andExpect(model().attribute("order", "asc"));

        // Serviceへ正しい条件が渡されたか確認
        verify(reservationService)
                .searchReservations("山田", null, "asc");
    }


    // 予約希望日をControllerがLocalDateへ変換できることを確認する
    @Test
    void showReservationList_予約希望日検索条件を受け取れる()
            throws Exception {

        LocalDate date = LocalDate.of(2026, 9, 30);

        when(reservationService.searchReservations(
                null,
                date,
                "asc"
        )).thenReturn(List.of());

        mockMvc.perform(
                        get("/reservations")
                                .param("preferredDate", "2026-09-30")
                                .param("order", "asc")
                )
                .andExpect(status().isOk())
                .andExpect(view().name("reservations/list"))
                .andExpect(
                        model().attribute(
                                "preferredDate",
                                "2026-09-30"
                        )
                );

        verify(reservationService)
                .searchReservations(null, date, "asc");
    }


    // 遠い順がServiceへ渡されることを確認する
    @Test
    void showReservationList_遠い順を指定できる()
            throws Exception {

        when(reservationService.searchReservations(
                null,
                null,
                "desc"
        )).thenReturn(List.of());

        mockMvc.perform(
                        get("/reservations")
                                .param("order", "desc")
                )
                .andExpect(status().isOk())
                .andExpect(view().name("reservations/list"))
                .andExpect(model().attribute("order", "desc"));

        verify(reservationService)
                .searchReservations(null, null, "desc");
    }


    // 氏名・予約希望日・並び順を同時に指定できることを確認する
    @Test
    void showReservationList_複数条件を同時に指定できる()
            throws Exception {

        LocalDate date = LocalDate.of(2026, 9, 30);

        Reservation reservation = new Reservation();
        reservation.setCustomerName("山田太郎");
        reservation.setPreferredDate(date);

        when(reservationService.searchReservations(
                "山田",
                date,
                "desc"
        )).thenReturn(List.of(reservation));

        mockMvc.perform(
                        get("/reservations")
                                .param("customerName", "山田")
                                .param(
                                        "preferredDate",
                                        "2026-09-30"
                                )
                                .param("order", "desc")
                )
                .andExpect(status().isOk())
                .andExpect(view().name("reservations/list"))

                // 検索結果がModelへ渡されているか
                .andExpect(
                        model().attributeExists("reservations")
                )

                // 検索条件がすべて保持されているか
                .andExpect(
                        model().attribute(
                                "customerName",
                                "山田"
                        )
                )
                .andExpect(
                        model().attribute(
                                "preferredDate",
                                "2026-09-30"
                        )
                )
                .andExpect(
                        model().attribute(
                                "order",
                                "desc"
                        )
                );

        // Serviceに3条件が正しく渡されたか確認
        verify(reservationService)
                .searchReservations(
                        "山田",
                        date,
                        "desc"
                );
    }
}