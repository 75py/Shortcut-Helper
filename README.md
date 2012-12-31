Shortcut-Helper
===============

<p>com.android.launcher.action.INSTALL_SHORTCUTを受信して保存、android.intent.action.CREATE_SHORTCUTから作成できるようにするアプリ。</p>

<p>Playストアのインストール後にショートカットを作成する機能や、Chromeのブックマーク長押しで出る「ホーム画面に追加」あたりが該当します。このアプリを使うことで、ランチャーではないアプリ（SwipePadなど）にも同じショートカットを作成することが可能になります。</p>

##使い方

1. ShortcutHelper.apkをインストールする
2. com.android.launcher.action.INSTALL_SHORTCUTのブロードキャストを送信する<br />この時、Shortcut Helperがショートカットの情報を保存します。
3. ランチャーなどからショートカット作成でこのアプリを呼び出す

<p>保存したやつの削除機能はめんどくさいので付けていません。アプリを再インストールするなりデータ消すなりしてください。</p>
