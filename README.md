# Android Movie Streaming

Klien Java Android untuk katalog film/serial dan pemutar video berbasis URL. Source lokal berasal dari [Nimith-Nimeshana/MovieStreamingApp](https://github.com/Nimith-Nimeshana/MovieStreamingApp). Repo ini merupakan ekspor studi dengan perbaikan terbatas; atribusi source dan aset dipertahankan di [NOTICE.md](NOTICE.md).

## Yang tersedia

- Form register dan login melalui Firebase Authentication email/password; logout memanggil `signOut`.
- Slider, daftar film/serial, detail, cast, dan episode dari Firebase Realtime Database.
- Pencarian judul atau teks field `Fcast` tanpa membedakan huruf besar/kecil, dibatasi 15 hasil. Field cast pada source juga dipakai sebagai key data cast; pencarian belum menelusuri nama pemeran di node `cast`.
- Hasil pencarian dapat dibuka ke detail melalui key record Firebase. Dua film dengan judul sama tetap menunjuk record masing-masing.
- Pemutaran URL HTTP/HTTPS melalui ExoPlayer 2.19.1. Posisi dan pilihan play/pause dipertahankan saat layar dibuat ulang atau aplikasi kembali dari background.
- Activity katalog, detail, pencarian, profil, dan pemutar memeriksa akun Firebase saat dibuka/kembali aktif. Guard ini tidak menggantikan rules otorisasi database di server.

Tanpa konfigurasi Firebase milik pengembang, aplikasi menampilkan petunjuk konfigurasi lokal. Akun, database, rules server, katalog, file video, dan kredensial Firebase asli tidak disertakan. Keberadaan kode fitur di atas belum menjadi bukti bahwa layanan tersebut telah diuji dengan backend nyata.

## Build dan jalankan

Gunakan JDK 17 dan Android SDK Platform 35. Konfigurasi memakai AGP 8.8.2, Gradle wrapper 8.10.2, minimum API 24, dan target API 34.

```powershell
.\gradlew.bat :app:assembleDebug :app:testDebugUnitTest :app:lintDebug
adb install -r app\build\outputs\apk\debug\app-debug.apk
```

SDK diatur melalui Android Studio atau `ANDROID_HOME`. `local.properties` tidak ikut repo. Cache Gradle dan SDK dapat dipindahkan ke drive D melalui `GRADLE_USER_HOME` dan `ANDROID_HOME`. Build pertama dapat mengunduh dependency. Slider memakai koordinat author `com.github.smarteist:Android-Image-Slider:1.4.0` dari JitPack. Repository dibatasi untuk grup slider. Rujukan: [source author](https://github.com/smarteist/Android-Image-Slider) dan [POM versi 1.4.0](https://jitpack.io/com/github/smarteist/Android-Image-Slider/1.4.0/Android-Image-Slider-1.4.0.pom).

Dependency player memakai `com.google.android.exoplayer:exoplayer:2.19.1` melalui repository `google()`. [POM resmi Google Maven](https://dl.google.com/dl/android/maven2/com/google/android/exoplayer/exoplayer/2.19.1/exoplayer-2.19.1.pom) telah diverifikasi tersedia; versi 2.11.7 dari source lama tidak dapat diambil saat build persiapan. Player disesuaikan ke `ExoPlayer.Builder`, `MediaItem`, dan `Player.Listener`; posisi serta status pause tetap disimpan. Menurut [rilis resmi 2.19.1](https://github.com/google/ExoPlayer/releases/tag/r2.19.1), ini rilis terakhir yang direncanakan untuk namespace lama; migrasi ke AndroidX Media3 tetap menjadi pekerjaan berikutnya.

Glide memakai versi 4.16.0. [Rilis upstream](https://github.com/bumptech/glide/releases/tag/v4.16.0) menyertakan perbaikan anotasi permission pada `NotificationTarget`. Aplikasi ini hanya memuat gambar ke view dan tidak memakai target notifikasi tersebut, sehingga tidak meminta permission notifikasi untuk menutupi error lint dari dependency lama. Adapter film, serial, dan episode juga mengikat nilai item saat kartu ditampilkan; aksi klik tidak memakai posisi daftar yang mungkin sudah berubah.

## Memakai Firebase sendiri

1. Daftarkan aplikasi Android pada proyek Firebase milikmu dengan applicationId `com.example.flixproject`.
2. Letakkan konfigurasi yang kamu unduh pada `app/google-services.json`. File ini diabaikan Git. Plugin Google Services hanya aktif ketika file tersebut ada; build tanpa file tetap disediakan untuk layar petunjuk lokal.
3. Aktifkan metode Authentication email/password dan buat akun uji milikmu.
4. Siapkan Realtime Database, aturan akses yang sesuai untuk akun uji, dan katalog dengan field yang diharapkan source. Jangan membuka akses tulis publik atau memakai dokumen/data pengguna sungguhan untuk demo.
5. Build ulang. Gunakan video dan gambar yang memang boleh kamu gunakan; URL contoh atau akun upstream tidak disediakan.

Bentuk data dibaca dari model berikut. Nama field mengikuti source, termasuk huruf besarnya.

| Node | Field utama |
| --- | --- |
| `movies/{id}` | `Mtitle`, `Mthumb`, `Mvid` untuk slider |
| `films/{id}` | `Ftitle`, `Fthumb`, `Fcover`, `Fdis`, `Fcast`, `Flink` |
| `series/{id}` | `Stitle`, `Sthumb`, `Scover`, `Sdis`, `Scast`, `Slink`, `Sepisodes` |
| `cast/{castKey}/{id}` | `Cname`, `Clink` |
| `link/{videoKey}` | String URL video |
| `link/{episodesKey}/{id}` | `Epart`, `Ethumb`, `Evid` |

`Fcast`/`Scast` menunjuk key cast; `Flink`/`Slink` menunjuk key video; `Sepisodes` menunjuk kumpulan episode. Katalog dibaca sebagai snapshot saat layar dibuka. Pencarian masih mengambil node `films` di sisi klien untuk setiap query; belum ada pencarian server atau pagination.

## Peta kode dan perubahan

| Berkas | Fungsi |
| --- | --- |
| `app/src/main/java/com/example/flixproject/FirebaseSupport.java` | Guard konfigurasi dan akun sebelum instance/layar Firebase dipakai |
| `app/src/main/java/com/example/flixproject/AuthenticatedActivity.java` | Pemeriksaan sesi saat start dan listener auth yang dilepas saat stop |
| `app/src/main/java/com/example/flixproject/MainActivity.java` | Slider dan ringkasan katalog |
| `app/src/main/java/com/example/flixproject/SearchActivity.java` | Query pencarian dan penolakan respons query lama |
| `app/src/main/java/com/example/flixproject/MovieSearch.java` | Pencocokan teks, limit hasil, dan pemeliharaan identitas record |
| `app/src/main/java/Models/FilmSearchResult.java` | Key Firebase asli beserta data film; tidak menggunakan judul sebagai ID |
| `app/src/main/java/Adapters/` dan `Models/` | Tampilan daftar dan bentuk data Firebase |
| `app/src/main/java/com/example/flixproject/PlayerActivity.java` | Validasi URL dan lifecycle pemutar |

Perbaikan menutup bug thumbnail/cast tertukar, logout yang hanya berganti layar, penumpukan listener realtime, record null, dan pelepasan player ganda. Klik daftar tidak lagi memakai posisi adapter yang sudah berubah. Constraint layout diperbaiki dan lebar daftar menyesuaikan ruang layar. Tiga deklarasi Activity yang tidak mempunyai kelas serta deklarasi compileSdk yang bertentangan juga dihapus. Warna dan aset utama tetap mengikuti source sebelumnya.

## Verifikasi dan batasan

Pada 27 Oktober 2026, `assembleDebug`, `testDebugUnitTest`, dan `lintDebug` berhasil dijalankan dengan JDK 17 serta SDK 35. APK debug terbentuk, enam unit test lulus, dan lint mencatat 0 error serta 146 warning. Tes memeriksa judul/cast, field null, query kosong, judul kembar dengan ID berbeda, batas hasil, dan penolakan key record yang mengandung path lain. Rincian ada di [hasil pemeriksaan](BUILD-VERIFICATION.md).

Pemulihan play/pause, alur sesi, login, rules Firebase, dan playback layanan nyata belum diuji pada emulator atau HP. Unit test pencarian tidak membuktikan alur tersebut.

Belum ada screenshot runtime lokal. State kosong/loading/error pada seluruh layar katalog juga belum dilengkapi; layar petunjuk konfigurasi dan pesan data/video tidak tersedia hanya menangani kasus yang disebutkan di atas. Firebase dan slider masih mengikuti stack lama; ExoPlayer 2.19.1 juga sudah deprecated. Dependency perlu peninjauan tersendiri sebelum penggunaan produksi. Manifest mempertahankan izin HTTP dari source; gunakan HTTPS untuk media nyata dan persempit kebijakan jaringan sesuai host yang kamu kelola. Repo ini tidak membundel hak tayang atau lisensi konten film.
