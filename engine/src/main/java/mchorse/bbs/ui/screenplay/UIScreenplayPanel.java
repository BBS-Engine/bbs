package mchorse.bbs.ui.screenplay;

import mchorse.bbs.game.utils.ContentType;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.screenplay.Screenplay;
import mchorse.bbs.screenplay.tts.ElevenLabsAPI;
import mchorse.bbs.screenplay.tts.TTSGenerateResult;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.dashboard.UIDashboard;
import mchorse.bbs.ui.dashboard.panels.UIDataDashboardPanel;
import mchorse.bbs.ui.framework.elements.buttons.UIIcon;
import mchorse.bbs.ui.framework.elements.input.text.UITextEditor;
import mchorse.bbs.ui.framework.elements.overlay.UIMessageFolderOverlayPanel;
import mchorse.bbs.ui.framework.elements.overlay.UIOverlay;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.utils.Direction;
import mchorse.bbs.utils.colors.Colors;

public class UIScreenplayPanel extends UIDataDashboardPanel<Screenplay>
{
    public UITextEditor screenplay;

    public UIIcon generate;

    public UIScreenplayPanel(UIDashboard dashboard)
    {
        super(dashboard);

        this.screenplay = new UITextEditor((t) -> this.data.content = t);
        this.screenplay.highlighter(new FountainSyntaxHighlighter()).background().relative(this.editor).full();
        this.screenplay.wrap();

        this.generate = new UIIcon(Icons.SOUND, (b) -> this.generateTTS());
        this.generate.tooltip(IKey.lazy("Generate voice lines (ElevenLabs TTS)"), Direction.LEFT);

        this.editor.add(this.screenplay);
        this.iconBar.add(this.generate);

        this.fill(null);
    }

    private void generateTTS()
    {
        try
        {
            ElevenLabsAPI.generate(this.data, (result) ->
            {
                if (result.status == TTSGenerateResult.Status.INITIALIZED)
                {
                    this.getContext().notify(IKey.lazy("Starting TTS generation!"), Colors.BLUE | Colors.A100);
                }
                else if (result.status == TTSGenerateResult.Status.GENERATED)
                {
                    this.getContext().notify(IKey.lazy(result.message), Colors.BLUE | Colors.A100);
                }
                else if (result.status == TTSGenerateResult.Status.ERROR)
                {
                    this.getContext().notify(IKey.lazy("An error has occurred when generating a voice line: " + result.message), Colors.RED | Colors.A100);
                }
                else if (result.status == TTSGenerateResult.Status.TOKEN_MISSING)
                {
                    this.getContext().notify(IKey.lazy("You haven't specified a token in BBS' settings!"), Colors.RED | Colors.A100);
                }
                else if (result.status == TTSGenerateResult.Status.VOICE_IS_MISSING)
                {
                    this.getContext().notify(!result.missingVoices.isEmpty()
                        ? IKey.lazy("A voice " + result.missingVoices.get(0) + " provided in the screenplay isn't available!")
                        : IKey.lazy("A list of voices couldn't get loaded!"),
                        Colors.RED | Colors.A100);
                }
                else /* SUCCESS */
                {
                    UIOverlay.addOverlay(this.getContext(), new UIMessageFolderOverlayPanel(UIKeys.SUCCESS, IKey.lazy("Voice lines were successfully generated!"), result.folder));
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    protected IKey getTitle()
    {
        return IKey.lazy("Screenplays");
    }

    @Override
    public ContentType getType()
    {
        return ContentType.SCREENPLAY;
    }

    @Override
    public void fill(Screenplay data)
    {
        super.fill(data);

        this.generate.setEnabled(data != null);

        if (data != null)
        {
            this.screenplay.setText(data.content);
        }
    }
}