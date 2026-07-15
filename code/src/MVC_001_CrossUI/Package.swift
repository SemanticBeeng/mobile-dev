// swift-tools-version: 5.10
import PackageDescription

let package = Package(
    name: "MVC_001_CrossUI",
    platforms: [.macOS(.v14)],
    dependencies: [
        .package(
            url: "https://github.com/moreSwift/swift-cross-ui",
            .upToNextMinor(from: "0.2.0")
        ),
        .package(path: "../MVC_001_SharedModels")
    ],
    targets: [
        .executableTarget(
            name: "MVC_001_CrossUI",
            dependencies: [
                .product(name: "SwiftCrossUI", package: "swift-cross-ui"),
                .product(name: "GtkBackend", package: "swift-cross-ui"),
                "SharedModels"
            ]
        ),
        .testTarget(
            name: "MVC_001_CrossUITests",
            dependencies: ["MVC_001_CrossUI"]
        )
    ]
)
