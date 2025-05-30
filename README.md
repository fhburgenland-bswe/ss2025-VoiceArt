# 🎨 VoiceGames

**VoiceGames** is a Java application that allows you to create art using your **voice**. Unleash your creativity in a whole new way!

---

## 📦 Installation

To install and run the application:

1. Go to the [Releases] tab.
2. Download the latest `.zip` file.
3. Extract the contents to a folder of your choice.
4. Inside the folder, you’ll find an executable.

---

## 🚀 Usage

Coming soon... Stay tuned! 🎤🖌️

---

## 🤝 Contributing

Pull requests are always welcome!  
Please note that the `main` branch is protected — create a feature branch for your changes.

We use **Checkstyle** with the **Google Java Style Guide** and enforce formatting using **Spotless**.

### 🧼 Formatting your code
```bash
./gradlew spotlessApply
```

### ✅ Checking code style
```bash
./gradlew checkstyleMain checkstyleTest
```

Make sure to update or add tests as needed when submitting changes.

---

## 📦 Release

To create a new version of the game:

1. Head to the [Releases] tab.
2. Click **“Draft a new release”**.
3. Use the version format `x.y.z`:
    - `x` → Major (breaking changes)
    - `y` → Minor (new features)
    - `z` → Patch (bug fixes)

📦 Once published, a GitHub Action will automatically:
- Build the project
- Package it into a `.zip`
- Upload the file to the release

---

## 📄 License

This project is licensed under the [GPL-3.0 License](https://choosealicense.com/licenses/gpl-3.0/).

[Releases]: https://github.com/fhburgenland-bswe/ss2025-VoiceArt/releases