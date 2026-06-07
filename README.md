# Test Automation - Parallel Execution

## Apa itu Parallel Execution?

Bayangkan kamu sedang mengerjakan ujian yang terdiri dari 10 soal. Jika kamu mengerjakannya **satu per satu** (sequential), kamu butuh waktu lama. Tapi kalau kamu punya **10 teman** dan masing-masing mengerjakan 1 soal secara **bersamaan** (parallel), semua soal bisa selesai jauh lebih cepat.

Konsep yang sama berlaku di test automation:

| Pendekatan | Cara Kerja | Waktu |
|------------|-----------|-------|
| **Sequential** (tanpa parallel) | Test A selesai → baru Test B jalan → baru Test C jalan | Lama ⏳ |
| **Parallel** (dengan parallel) | Test A, B, dan C jalan **bersamaan** di browser berbeda | Cepat ⚡ |

### Contoh Nyata di Project Ini

**Tanpa parallel:**
```
[Browser 1] bookstore.LoginTests  (30 detik)
                                    ↓ selesai, baru lanjut
[Browser 1] saucedemo.LoginTests  (30 detik)
                                    ↓
Total: 60 detik
```

**Dengan parallel:**
```
[Browser 1] bookstore.LoginTests  (30 detik)  ──┐
[Browser 2] saucedemo.LoginTests  (30 detik)  ──┤ jalan bareng!
                                                  ↓
Total: ~30 detik (2x lebih cepat!)
```

### Kenapa Bisa Jalan Bareng?

Kunci utamanya adalah **ThreadLocal**. Secara sederhana:
- Setiap "thread" (jalur eksekusi) memiliki **browser sendiri-sendiri** yang tidak saling ganggu
- Thread 1 buka Chrome untuk test bookstore, Thread 2 buka Chrome lain untuk test saucedemo
- Mereka bekerja secara **independen** dan **tidak saling tunggu**

Tanpa ThreadLocal, semua test akan berebut 1 browser yang sama dan hasilnya akan kacau.

### Istilah Penting untuk Pemula

| Istilah | Penjelasan Sederhana |
|---------|---------------------|
| **Thread** | Jalur eksekusi program. Bayangkan seperti "pekerja" yang menjalankan tugas |
| **ThreadLocal** | Kotak penyimpanan pribadi untuk setiap pekerja, tidak bisa diakses pekerja lain |
| **Thread Count** | Jumlah pekerja yang bekerja bersamaan. Makin banyak = makin cepat, tapi butuh RAM lebih besar |
| **Suite File** | File XML yang mengatur test mana yang dijalankan dan bagaimana cara menjalankannya |
| **WebDriver** | "Remote control" yang digunakan program untuk mengendalikan browser |

---

## Overview

Project ini adalah framework test automation menggunakan **Java + Selenium + TestNG + Gradle** yang mendukung **parallel test execution** untuk mempercepat waktu eksekusi test.

## Arsitektur Parallel Execution

### Thread Safety

Framework ini menggunakan `ThreadLocal<WebDriver>` pada `DriverManager` sehingga setiap thread memiliki instance browser sendiri, aman untuk dijalankan secara paralel.

```
DriverManager (ThreadLocal<WebDriver>)
├── Thread-1 → Chrome Instance 1 → bookstore.LoginTests
├── Thread-2 → Chrome Instance 2 → saucedemo.LoginTests
└── Thread-3 → Chrome Instance 3 → (test lainnya)
```

### Level Parallelism yang Tersedia

| Level | Deskripsi | Suite File |
|-------|-----------|------------|
| `parallel="tests"` | Setiap `<test>` tag berjalan di thread berbeda | `smoke.xml` |
| `parallel="classes"` | Setiap test class berjalan di thread berbeda | `parallel.xml` |
| `parallel="methods"` | Setiap test method berjalan di thread berbeda | Custom |

## Implementasi Parallel di Project Ini

### Langkah 1: DriverManager dengan ThreadLocal

File `DriverManager.java` menggunakan `ThreadLocal<WebDriver>` agar setiap thread punya browser sendiri:

```java
private static final ThreadLocal<WebDriver> driver = new ThreadLocal<>();
```

Ini adalah fondasi utama agar parallel bisa berjalan dengan aman.

### Langkah 2: BaseTests sebagai Induk Semua Test

Setiap test class (`bookstore.LoginTests`, `saucedemo.LoginTests`) meng-extend `BaseTests`. Di sinilah browser dibuka (`@BeforeMethod`) dan ditutup (`@AfterMethod`) untuk setiap test method.

### Langkah 3: Konfigurasi Suite XML

Di file `smoke.xml`, cukup tambahkan atribut `parallel` dan `thread-count`:

```xml
<suite name="SmokeTestSuite" parallel="tests" thread-count="3">
```

Artinya: jalankan setiap `<test>` tag secara paralel, maksimal 3 thread bersamaan.

### Langkah 4: Gradle Parallel Forks

Di `build.gradle.kts`, ditambahkan:

```kotlin
maxParallelForks = Runtime.getRuntime().availableProcessors().coerceAtMost(4)
```

Artinya: Gradle boleh menjalankan hingga 4 proses test secara bersamaan (tergantung jumlah CPU).

---

## Suite Files

### 1. `smoke.xml` — Parallel by Tests
Menjalankan `BookstoreLoginTests` dan `SaucedemoLoginTests` secara paralel di thread yang berbeda.

```xml
<suite name="SmokeTestSuite" parallel="tests" thread-count="3">
```

### 2. `parallel.xml` — Parallel by Classes
Menjalankan semua test class secara paralel dalam satu `<test>` tag.

```xml
<suite name="ParallelTestSuite" parallel="classes" thread-count="4">
```

## Cara Menjalankan

### Menjalankan Smoke Test (Parallel by Tests)

**macOS / Linux:**
```bash
./gradlew test -Psuite=smoke.xml
```

**Windows:**
```bash
gradlew.bat test -Psuite=smoke.xml
```

### Menjalankan Parallel by Classes

**macOS / Linux:**
```bash
./gradlew test -Psuite=parallel.xml
```

**Windows:**
```bash
gradlew.bat test -Psuite=parallel.xml
```

### Menjalankan dengan Environment Tertentu

```bash
./gradlew test -Psuite=smoke.xml -Penv=production
```

### Mengatur Thread Count via Command Line

Edit `thread-count` di file XML suite sesuai kebutuhan:
- **2-3 threads** untuk laptop/PC biasa
- **4+ threads** untuk mesin dengan resource lebih besar

## Konfigurasi Gradle

`build.gradle.kts` juga mendukung parallelism di level Gradle:

```kotlin
maxParallelForks = Runtime.getRuntime().availableProcessors().coerceAtMost(4)
```

Ini memungkinkan Gradle menjalankan multiple test worker secara bersamaan.

## Struktur Project

```
src/test/
├── java/
│   ├── core/
│   │   ├── BaseTests.java        # Base class dengan setup/teardown
│   │   ├── DriverManager.java    # ThreadLocal WebDriver (thread-safe)
│   │   ├── ConfigReader.java     # Load properties file
│   │   ├── TestListener.java     # ExtentReports listener
│   │   └── RetryAnalyzer.java    # Retry failed tests
│   ├── bookstore/
│   │   └── LoginTests.java       # Bookstore login tests
│   └── saucedemo/
│       └── LoginTests.java       # Saucedemo login tests
└── resources/
    ├── config/
    │   ├── staging.properties
    │   └── production.properties
    └── suites/
        ├── smoke.xml              # Parallel by tests
        └── parallel.xml           # Parallel by classes
```

## Reports

Setelah test selesai:
- **Gradle Report:** `build/reports/tests/test/index.html`
- **Extent Report:** `reports/extent-report_*.html`
- **Screenshots:** `reports/screenshots/`

## Catatan Penting

1. Pastikan resource PC/laptop cukup untuk menjalankan multiple browser secara bersamaan
2. Gunakan `thread-count` yang sesuai dengan kemampuan mesin
3. Setiap thread membuka browser instance baru, jadi RAM usage akan bertambah
4. `@BeforeSuite` hanya dijalankan sekali meskipun parallel, config loading tetap aman

