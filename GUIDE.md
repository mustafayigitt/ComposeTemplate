# ComposeTemplate Geliştirme Rehberi 🚀

Bu rehber, ComposeTemplate kullanarak nasıl hızlıca yeni özellikler ekleyebileceğinizi ve projeyi nasıl genişletebileceğinizi anlatır.

## 1. Yeni Bir Özellik (Feature) Ekleme

Yeni bir özellik eklemek için hazır scaffolding görevini kullanın:

```bash
./gradlew scaffoldFeature -PfeatureName=settings
```

Bu komut şunları otomatik yapar:
- `feature/settings` dizinini ve 4 alt modülünü oluşturur.
- Modülleri `settings.gradle.kts` dosyasına dahil eder.
- Modül bağımlılıklarını `app/build.gradle.kts` dosyasına ekler.
- Temel navigasyon rotasını (`SettingsRoute`) oluşturur.

## 2. Navigasyon Kaydı

`scaffoldFeature` komutu `navigation` modülünde route'u, `presentation` modülünde `IScreenProvider` binding'ini oluşturur. Elle ekleme yapmanız gerekirse pattern şu olmalıdır:

```kotlin
class SettingsScreenProvider @Inject constructor() : IScreenProvider {
    @Composable
    override fun provideScreen(route: INavigationItem, navigationManager: INavigationManager): Boolean =
        when (route) {
            is SettingsRoute -> {
                SettingsScreen(navigationManager)
                true
            }
            else -> false
        }
}
```

## 3. Mimari Standartlar

### ViewModel Yapısı
Tüm ViewModel'ler `BaseViewModel` sınıfından türemelidir:

```kotlin
@HiltViewModel
class MyViewModel @Inject constructor() : BaseViewModel<MyUiState, MyEvent>() {
    override val uiStateInternal = MutableStateFlow(MyUiState())

    fun doSomething() {
        updateState { it.copy(isLoading = true) }
        // ...
    }
}
```

### UI State ve Event
- **UiState:** Ekranın tüm durumunu temsil eden tek bir data class.
- **Event:** Snackbar gösterimi, navigasyon gibi bir kerelik olaylar için `Channel` tabanlı sistem.
- **Route/UI ayrımı:** Route composable ViewModel, lifecycle collection, event ve navigasyonu bağlar. Plain UI composable sadece immutable state ve callback alır.

## 4. Secret Management

Yeni bir API anahtarı veya client config değeri eklemek için:
1. `secrets.properties` dosyasına anahtarı ekleyin.
2. `./gradlew validateSecrets` ile doğrulayın.
3. `SecretManager.kt` içinde yeni bir metod tanımlayın.
4. `native-lib.cpp` içinde JNI metodunu implemente edin.
5. Release için `./gradlew hardeningReport` ve `scanApkForSecrets` çıktısını kontrol edin.

Not: Client içine konan değer mutlak güvenli değildir; bu yapı native obfuscation, runtime integrity checks ve MITM korumalarıyla reverse engineering maliyetini artıran defense-in-depth katmanıdır.

## 5. UI Bileşenleri

Ortak bileşenleri `:core:ui` modülünde bulabilirsiniz:
- `AppButton`: Standart buton.
- `AppLoading`: Yükleme göstergesi.
- `AppErrorState`: Hata durumları için hazır ekran.
- `AppCard`: Gölgelendirmesi ayarlanmış kart yapısı.

## 6. Onboarding ve Akış Kontrolü

Uygulama açılışında `:feature:splash` start destination kararını verir. Onboarding akışı `:feature:onboarding` altında data/domain/navigation/presentation olarak ayrılmıştır.

Yeni bir onboarding adımı eklerken genellikle şu dosyaları birlikte güncelleyin:
- `feature/onboarding/presentation/.../OnboardingUiState.kt`
- `feature/onboarding/presentation/.../OnboardingRoute.kt`
- `feature/onboarding/presentation/src/main/res/values/strings.xml`
- Gerekirse `feature/onboarding/domain` use case'leri ve `feature/onboarding/data` repository implementasyonu

## 7. İzin Yönetimi (Permissions)

`:core:permission` modülü ile izinleri kolayca yönetebilirsiniz:

```kotlin
PermissionRequired(
    permission = Manifest.permission.CAMERA,
    rationaleTitle = "Camera permission",
    rationaleMessage = "Camera access is needed to scan codes.",
    permanentlyDeniedTitle = "Permission disabled",
    permanentlyDeniedMessage = "Enable camera permission from settings.",
    onPermissionGranted = {
        CameraContent()
    },
    onPermissionDenied = { status, requestPermission ->
        PermissionDeniedContent(
            status = status,
            onRequestPermission = requestPermission,
        )
    },
)
```

## 8. Premium UI ve Google Play Özellikleri

### Shimmer (Yükleme Efekti)
Herhangi bir bileşene `Modifier.shimmer()` ekleyerek profesyonel bir yükleme efekti verebilirsiniz.

### In-App Review
`ReviewManager`'ı inject ederek kullanıcıdan puan isteyebilirsiniz:
```kotlin
reviewManager.requestReview(activity)
```

### App Update
`UpdateManager` ile hem Google Play güncellemelerini hem de Remote Config tabanlı "Zorunlu Güncelleme" (Force Update) mantığını yönetebilirsiniz.

## 9. Statik Analiz ve Kalite

Kodunuzu göndermeden önce mutlaka şu komutları çalıştırın:
```bash
./gradlew ktlintCheck detekt
./gradlew testDebugUnitTest assembleDebug :app:assembleRelease
```
