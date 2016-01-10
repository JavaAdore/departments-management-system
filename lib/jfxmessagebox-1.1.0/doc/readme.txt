= JfxMessageBox =
JfxMessageBox is a free MessageBox library for JavaFX 2.

[[Embed(HelloJfxMessageBox.png)]]

== Home page ==
http://sourceforge.jp/projects/jfxmessagebox/wiki/

== Download ==
[[ReleaseList]]

== System Requirements ==
JavaFX 2.2 or later is needed.

== Supported Locale ==
 * en, de, es, fr, it, ja, ko, pt, ru, zh

== Licensing ==
JfxMessageBox is licensed under a LGPL/EPL/ASL triple license, allowing use of the files under the terms of any one of the GNU Lesser General Public License, the Eclipse Public License, or the Apache License.

== API Reference ==
[http://svn.sourceforge.jp/svnroot/jfxmessagebox/trunk/JfxMessageBox/javadoc/jfx/messagebox/MessageBox.html JavaDoc]

== Source Code ==
[http://svn.sourceforge.jp/svnroot/jfxmessagebox/trunk/JfxMessageBox/ Source Code]

= GettingStarted =
Using JfxMessageBox is very easy.

{{{ code java
import jfx.messagebox.MessageBox;

    MessageBox.show(primaryStage,
        "Sample of information dialog.\n\nDialog option is below.\n[MessageBox.ICON_INFORMATION | MessageBox.OK | MessageBox.CANCEL]",
        "Information dialog",
        MessageBox.ICON_INFORMATION | MessageBox.OK | MessageBox.CANCEL);
}}}
[[Embed(InformationDialogSample.png)]]

For more infomation, see GettingStarted.