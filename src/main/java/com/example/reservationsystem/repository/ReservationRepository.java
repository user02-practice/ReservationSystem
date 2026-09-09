package com.example.reservationsystem.repository;

// Reservation Entityを使用するためにインポートする
import com.example.reservationsystem.entity.Reservation;

// Spring Data JPAの基本的なデータベース操作を使用するためにインポートする
import org.springframework.data.jpa.repository.JpaRepository;

// 複数件の検索結果を扱うために使用する
import java.time.LocalDate;
import java.util.List;

// 予約情報のデータベース操作を担当するRepository
public interface ReservationRepository
        extends JpaRepository<Reservation, Long> {

    // 氏名に指定した文字列を含む予約を検索する
    // 例：「山田」で検索すると「山田太郎」「山田花子」などが対象になる
    List<Reservation> findByCustomerNameContaining(String customerName);

    // 指定した予約希望日の予約を検索する
    List<Reservation> findByPreferredDate(LocalDate preferredDate);

    // 予約希望日が近い順（昇順）で予約一覧を取得する
    List<Reservation> findAllByOrderByPreferredDateAsc();

    // 予約希望日が遠い順（降順）で予約一覧を取得する
    List<Reservation> findAllByOrderByPreferredDateDesc();



}
