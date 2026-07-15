// swift-tools-version: 5.10
import PackageDescription

let package = Package(
    name: "MVC_001_SharedModels",
    products: [
        .library(
            name: "SharedModels",
            targets: ["SharedModels"]
        ),
    ],
    targets: [
        .target(
            name: "SharedModels"
        ),
        .testTarget(
            name: "SharedModelsTests",
            dependencies: ["SharedModels"]
        ),
    ]
)
