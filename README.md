#Shortcut-Helper

<p>com.android.launcher.action.INSTALL_SHORTCUTを受信して保存、android.intent.action.CREATE_SHORTCUTから作成できるようにするアプリです。</p>

<p>何のこっちゃと思う方もいらっしゃると思いますが……ショートカット作成方法は以下の二通りがあります。</p>

1. ランチャーから「ショートカット作成」的なやつを選択して、目的のアプリを選んで作成するパターン
2. アプリから「ショートカットを作成」的なボタンを押して、インストールされてるホームアプリにショートカットが作られるパターン

<p>んー……説明難しいです。要するに、NovaやらADWやらの長押しでショートカットを作るのが前者、Playストアのインストール後にショートカットを作成する機能や、Chromeのブックマーク長押しで出る「ホーム画面に追加」あたりが後者に該当します。</p>

<p>後者は、 com.android.launcher.action.INSTALL_SHORTCUTというブロードキャストを送信して、ホームアプリがこれを受信しショートカットを作ります。ブロードキャストは送信対象を限定しないので、インストールされているすべてのホームにショートカットが作られます。</p>

<p>後者はランチャーではないアプリ（SwipePadなどのサブランチャー）にショートカットを作成できません。それを可能にするのがこのアプリです。</p>

##使い方（ChromeでSwipePadにブックマークのショートカットを作成する場合）

1. ShortcutHelper.apkをインストールする
2. Chromeを起動し、ショートカットを作りたいブックマークを長押し
3. 「ホーム画面に追加」<br />この時、Shortcut Helperもブロードキャストをちゃっかり受信して、ショートカットの情報を保存します。<br />ホームに作られたショートカットは不要なので、いらなければ手動で消してください。
4. SwipePadのショートカットを作りたいスロットを選び、「ショートカット」からこのアプリを選択
5. 保存済みショートカットの一覧が表示されるので、先ほど選んだブックマークをタップ

<p>保存したやつの削除機能はめんどくさいので付けていません。アプリを再インストールするなりデータ消すなりしてください。</p>
