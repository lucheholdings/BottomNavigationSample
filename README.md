# Bottom Navigation + Jetpack Navigation + Fragment の実装例

[Jetpack Navigation 2.4.0](https://developer.android.com/jetpack/androidx/releases/navigation?hl=ja#2.4.0) で導入された [Multiple Back Stacks](https://developer.android.com/guide/navigation/multi-back-stacks) に対応した BottomNavigationView と Fragment の組み合わせによる実装例です。
詳しい解説はソースコード内のコメントの他、[Giftmall Inside Blog の記事](https://inside.luchegroup.com/entry/2023/)を参照してください。

## 主要なファイル

- [MainActivity](https://github.com/lucheholdings/BottomNavigationSample/blob/main/app/src/main/java/jp/co/giftmall/sample/navigation/MainActivity.kt)
  - Multiple Back Stacks に基いて BottomNavigationView と Jetpack Navigation を連動させる各種実装を例示しています
- [mobile_navigation.xml](https://github.com/lucheholdings/BottomNavigationSample/blob/main/app/src/main/res/navigation/mobile_navigation.xml)
  - [Nested Navigation Graph](https://developer.android.com/guide/navigation/navigation-nested-graphs) を用いた Multiple Back Stacks の実現に必要な構成を例示しています

## 参考リンク

- [Multiple Back Stacks](https://developer.android.com/guide/navigation/multi-back-stacks)
- [Nested Navigation Graph](https://developer.android.com/guide/navigation/navigation-nested-graphs)
- [https://issuetracker.google.com/issues/194301895](https://issuetracker.google.com/issues/194301895)
- [https://issuetracker.google.com/issues/228201897](https://issuetracker.google.com/issues/228201897)
- [https://issuetracker.google.com/issues/273796472](https://issuetracker.google.com/issues/273796472)