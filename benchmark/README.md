# Benchmark Module

Performance measurement module using Jetpack Macrobenchmark.

## 🚀 Features
- **Startup Benchmarking**: Measures cold startup time for the application.
- **Trace Analysis**: Generates `.perfetto-trace` files for deep-dive performance analysis.
- **Automated Execution**: Configured to run on physical devices.

## 📁 Structure
- `src/main/java/com/ytapps/composetemplate/benchmark/StartupBenchmark.kt`: The core test measuring app launch.

## 🛠️ Usage

1. **Connect a Physical Device**: Macrobenchmarks should not be run on emulators as the results are not representative.
2. **Run Benchmarks**:
   ```bash
   ./gradlew :benchmark:connectedBenchmarkAndroidTest
   ```

### 📊 Understanding Results
After execution, results will be printed to the console:
- **TTID (Time To Initial Display)**: Time until the first frame is drawn.
- **TTFD (Time To Full Display)**: Time until the app is fully interactive (if reported).

Traces can be opened in Android Studio Profiler or at [ui.perfetto.dev](https://ui.perfetto.dev).

## 🛡️ Security Note
The benchmarks run against a special `benchmark` build type of the `:app` module. This variant is minified but signed with a debug key to ensure performance characteristics are close to the production release.
