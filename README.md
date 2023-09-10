# ComposeTemplate 🚀

![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)
[![LinkedIn](https://img.shields.io/badge/LinkedIn-Connect-blue)](https://www.linkedin.com/in/mustafayigitt/)

## Project Description

ComposeTemplate is a Jetpack Compose template application that follows Clean Architecture best practices. It simplifies the process of setting up a well-structured Compose application by providing a template with a predefined folder structure. ✨

## Getting Started 🛠️

To create a new project using this template, follow these steps:

1. Run the `initializer.sh` script.
2. Set the `applicationName` and `applicationId` as prompted.
3. The script will copy a new project with the given credentials 🚀

## Folder Structure 📂

The project adheres to a well-defined folder structure that promotes maintainability and organization of your Compose application.
* app
    * src
        * main
            * java/com/applicationId/applicationName
                * core
                    * api // Core api operations and needs
                        * DefaultInterceptor
                        * Result
                    * base // Base classes and interfaces
                        * BaseRepository
                        * BaseUiState
                        * IMapper
                    * di // Dependency Injection
                        * BinderModule
                        * ProviderModule
                    * navigation // Navigation layer
                        * IBottomBarItem
                        * INavigationItem
                        * NavigationManager
                    * theme // Theme operations and components
                        * component
                        * Color.kt
                        * Theme.kt
                        * Type.kt
                * data
                    * local // Local data operations
                    * remote // Remote data operations
                    * repository // Repository implementations
                    * model // Network models
                * domain
                    * model // UI models
                    * mapper // Network Model -> UI Model mappers
                    * repository // Repository definitions
                    * usecase // User stories, use-cases
                * ui // Features
                * util // Util functions, helper classes
                * App
                * MainActivity

## Contributing 🤝

We welcome contributions from the community! If you'd like to contribute to this project 🙌
- Fork this repository
- Clone your forked repository
- Create your feature branch (git checkout -b my-new-feature)
- Make changes
- Commit your changes (git commit -m 'Add some feature')
- Push to the branch (git push origin my-new-feature)
- Create a new Pull Request
- Wait for review and approval from maintainers

## License 📝

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

## Contact 📧

If you have any questions, suggestions, or want to report issues, feel free to open an issue here on GitHub. 
You can also connect with the project maintainer on [LinkedIn](https://www.linkedin.com/in/mustafayigitt/) 👋
