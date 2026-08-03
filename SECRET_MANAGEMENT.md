# Android Secret Management

Bu template, backend olmayan senaryolarda Android client icindeki hassas config degerlerinin cikarilma maliyetini artirmak icin katmanli bir secret management yaklasimi sunar.

Onemli sinir: public bir mobil uygulamanin icine konan deger, yeterince motivasyonu olan biri tarafindan cikarilabilir. Bu yapi secret'i imkansiz kilmaz; native obfuscation, runtime integrity checks ve MITM korumalariyla reverse engineering/decompile maliyetini artirir.

## Katmanlar

1. Local/CI secret loading
   - Local gelistirmede `secrets.properties` kullanilir.
   - CI icin ayni key isimleri environment variable olarak verilebilir.
   - `secrets.properties` git disinda tutulur.

2. Build-time validation
   - `validateSecrets` artik Android `preBuild` akislari oncesinde calisir.
   - Eksik deger, bos secret, `YOUR_` placeholder, kisa `XOR_MASK`, gecersiz SHA-256 signature hash ve slash'siz Retrofit base URL build'i durdurur.
   - Release build signing key eksikse warning degil fail olur.

3. Native secret obfuscation
   - `composetemplate.useNativeSecrets=true` iken API key ve base URL degerleri BuildConfig'e duz metin yazilmaz.
   - Degerler build sirasinda XOR ile obfuscate edilir ve generated C++ header'a byte array olarak yazilir.
   - XOR mask static header, CMake define ve Kotlin runtime parcasi olarak bolunur.
   - Native katman release'te app signature, emulator ve debugger sinyallerini kontrol edebilir.

4. Runtime integrity checks
   - `core:security` modulu app signature, package name, installer, emulator, debugger, root ve hooking sinyallerini toplar.
   - Debug build'lerde bulgular warn davranisi icin uygundur.
   - Release build'lerde `NATIVE_RUNTIME_CHECKS_ENABLED=true` ise startup block uygulanir.

5. Network/MITM hardening
   - Main network security config cleartext kapatir ve sadece system CA trust eder.
   - Debug kaynaklari localhost/10.0.2.2 cleartext ve user CA icin override saglar.
   - `CERTIFICATE_PINNING_ENABLED=true` oldugunda release OkHttp client primary + backup `sha256/...` pin bekler.

6. Artifact scan
   - `scanApkForSecrets` APK/AAB icinde ham `API_KEY_*` ve `BASE_URL_*` degerlerini arar.
   - Release `assembleRelease` ve `bundleRelease` sonrasi otomatik calisir.
   - `hardeningReport` aktif secret management/hardening ayarlarini yazdirir.

## secrets.properties

Yeni app'i urettikten sonra root dizinde ornek dosyadan baslayabilirsin:

```bash
cp secrets.properties.example secrets.properties
```

```properties
API_KEY_DEBUG="debug_key"
API_KEY_RELEASE="release_key"

BASE_URL_DEBUG="https://api-debug.example.com/"
BASE_URL_RELEASE="https://api.example.com/"

XOR_MASK="at_least_24_chars_mask_value"
EXPECTED_SIGNATURE_HASH="AABBCCDDEEFF00112233445566778899AABBCCDDEEFF00112233445566778899"
NATIVE_RUNTIME_CHECKS_ENABLED=true

CERTIFICATE_PINNING_ENABLED=false
CERTIFICATE_PINS="sha256/primaryBase64PinHereAAAAAAAAAAAAAAA=,sha256/backupBase64PinHereBBBBBBBBBBBBBBB="

STORE_FILE="release.keystore"
KEY_ALIAS="release_key_alias"
KEY_PASSWORD="release_key_password"
STORE_PASSWORD="release_store_password"
```

`EXPECTED_SIGNATURE_HASH` colon'lu veya colon'suz verilebilir; build ve native taraf bunu normalize eder. Google Play App Signing kullaniyorsan release icin Play Console'daki App signing key certificate SHA-256 hash'ini kullan.

## Pinning

OkHttp pin formatini kullan:

```properties
CERTIFICATE_PINNING_ENABLED=true
CERTIFICATE_PINS="sha256/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=,sha256/BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB="
```

En az iki pin gir: current pin ve backup/rotation pin. Pinning debug'da otomatik bypass edilir, release'te zorunlu calisir.

## Komutlar

```bash
./gradlew validateSecrets
./gradlew hardeningReport
./gradlew :core:secrets:assembleDebug
./gradlew :app:compileDebugKotlin
./gradlew :app:assembleRelease
```

## Release Checklist

- `./gradlew validateSecrets`
- `./gradlew hardeningReport`
- `EXPECTED_SIGNATURE_HASH` release signing cert ile uyumlu
- `BASE_URL_RELEASE` HTTPS ve trailing slash ile bitiyor
- `CERTIFICATE_PINNING_ENABLED=true` ise en az iki pin mevcut
- Release signing keyleri env var veya `secrets.properties` ile saglaniyor
- `./gradlew :app:assembleRelease` sonrasi `scanApkForSecrets` temiz geciyor

## Tehdit Modeli

Bu yapi su riskleri azaltmayi hedefler:

- JADX/decompile ile duz secret gorulmesi
- `strings` ile APK icinde ham key yakalanmasi
- Yanlis base URL veya placeholder ile artifact uretilmesi
- User CA uzerinden release MITM denemeleri
- Basit re-signed clone app'in release secret alabilmesi
- Debugger/emulator/root/hooking gibi runtime analiz sinyalleri

Bu yapi sunlari garanti etmez:

- Client icindeki secret'in sonsuza kadar gizli kalmasi
- Runtime memory'de acilan degerin hic yakalanamamasi
- Frida/Xposed/root kontrollerinin bypass edilememesi
- Patch'lenmis bir client'in native/Kotlin kontrollerini atlatamamasi
- Backend authorization, token expiration veya attestation yerine gecmesi

Gercek yuksek yetkili secret'lar backend, token exchange, expiration, Firebase/Supabase rules, Play Integrity/App Attest ve server-side kontrollerle korunmalidir.
