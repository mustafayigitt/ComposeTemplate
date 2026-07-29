# Android'de NDK ve Gradle ile Uçtan Uca Güvenli Secret Yönetimi

Android uygulamalarında API anahtarları gibi hassas verileri korumak, kedi-fare oyununa benzer. Bu makalede, ComposeTemplate projesinde kullanılan ve "Defense in Depth" (Derinlemesine Savunma) prensibini temel alan profesyonel secret yönetimi mimarisini inceleyeceğiz.

---

## 1. Problem: Neden Sadece Strings.xml Yetmez?

Çoğu geliştirici anahtarlarını `Strings.xml` veya `BuildConfig` içine koyar. Ancak:
- **Statik Analiz:** `strings` komutuyla binary içindeki tüm düz metinler okunabilir.
- **Decompile:** JADX ile Java koduna dönüştürülen bir APK'da anahtarlar "altın tepside" sunulur.
- **Güvenlik Açığı:** Anahtar bir kez çalındığında, uygulamanızın kimliğini taklit eden sahte istekler oluşturulabilir.

---

## 2. Mimari Çözüm: Katmanlı Savunma (STAR Analizi)

- **S (Situation):** Hassas verilerin APK içinden çalınma riski.
- **T (Task):** Verileri hem statik hem de dinamik analizlere karşı korumak.
- **A (Action):** Gradle'da XOR şifreleme, NDK'da imza doğrulamalı C++ katmanı kurulumu.
- **R (Result):** JADX ile okunamaz, klonlanamaz ve merkezi olarak yönetilen bir güvenlik sistemi.

---

## 3. Mimari Katmanlar

Çözümümüz 4 ana katmandan oluşur:
1. **Yerel Katman:** Verilerin Git dışında tutulması.
2. **Derleme Katmanı (Gradle):** Verilerin şifrelenip C++ katmanına enjekte edilmesi.
3. **Native Katman (NDK):** Bellek içinde şifre çözme ve imza doğrulama.
4. **Uygulama Katmanı (Kotlin):** Basit ve temiz bir API ile erişim.

---

## 4. Yapılandırma Seçenekleri

Güvenlik seviyesini `gradle.properties` üzerinden projenize göre ayarlayabilirsiniz:

```properties
# Yüksek Güvenlik (NDK + Hardening) - Önerilen
composetemplate.useNativeSecrets=true

# Standart Güvenlik (BuildConfig) - Hızlı Debug
composetemplate.useNativeSecrets=false
```

---

## 5. Teknik Uygulama Detayları

### A. Yerel Yapılandırma (`secrets.properties`)
Tüm sırlar projenin kök dizininde, asla VCS'ye (Git) girmeyen bir dosyada tutulur. `secrets.properties.example` dosyası bir şablon görevi görür.

```properties
API_KEY_RELEASE="4fde2..."
XOR_MASK="my_secret_mask_123"
EXPECTED_SIGNATURE_HASH="A1:B2:C3..."
```

### B. Gradle Validasyonu
`ValidateSecretsPlugin` sayesinde, bir anahtar eksikse veya placeholder (`YOUR_...`) olarak bırakılmışsa derleme anında hata alınır. Bu, hatalı APK üretimini engeller.

### C. Build-Time Şifreleme (XOR & Hex)
Veriler C++ katmanına ham halde gönderilmez. `AndroidLibraryNativeConventionPlugin` içerisinde XOR şifrelemesinden geçirilir ve Hex formatına dönüştürülür. Bu sayede binary içinde düz metin arayan araçlar sadece anlamsız Hex karakterleri görür.

### D. NDK ile Güvenlik Kilidi (C++)
`native-lib.cpp` içerisindeki en kritik nokta **JNI Signature Validation** işlemidir. C++ kodu, JNI üzerinden Android sistemine bağlanarak uygulamanın SHA-256 imza hash'ini sorgular:

```cpp
bool isSignatureValid(JNIEnv* env, jobject context) {
    // JNI üzerinden uygulamanın SHA-256 hash'ini al ve kontrol et
    if (actualHash != expectedHash) return false;
    return true;
}
```
**Neden Önemli?** Uygulamanızı biri kopyalayıp (clone) kendi imzasıyla imzalarsa, C++ katmanı bunu anlar ve anahtarları "UNAUTHORIZED_ACCESS" olarak döndürür.

### E. Kotlin API (`SecretManager`)
Tüm bu karmaşık yapı, uygulama tarafında `SecretManager` singleton nesnesi ile soyutlanır.

```kotlin
// Başlatma (App.kt)
SecretManager.initialize(this)

// Kullanım
val api = SecretManager.getApiKey()
```

---

## 5. Önemli İpuçları ve Uyarılar

> [!TIP]
> **XOR Maskesi Seçimi:** Maskenizi ne kadar uzun ve karmaşık tutarsanız, brute-force saldırılarına karşı o kadar dirençli olur. Proje kökündeki `XOR_MASK` değerini periyodik olarak değiştirmek güvenliği artırır.

> [!WARNING]
> **Release İmzası:** Uygulamanızı Google Play App Signing ile yayınlıyorsanız, `EXPECTED_SIGNATURE_HASH` değerine yerel keystore hash'inizi değil, **Google Play Console**'da "App Integrity" bölümünde bulunan "App signing key certificate" SHA-256 hash'ini yazmalısınız.

---

Bu yapı, bir Android uygulamasında güvenliği sadece bir ayar değil, projenin derleme döngüsüne (build lifecycle) entegre edilmiş bir sisteme dönüştürür.
