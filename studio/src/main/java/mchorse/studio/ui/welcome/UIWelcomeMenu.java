package mchorse.studio.ui.welcome;

import mchorse.bbs.BBSSettings;
import mchorse.bbs.bridge.IBridge;
import mchorse.bbs.graphics.text.FontRenderer;
import mchorse.bbs.graphics.texture.Texture;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.resources.Link;
import mchorse.bbs.ui.framework.UIBaseMenu;
import mchorse.bbs.ui.framework.UIRenderingContext;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.buttons.UIButton;
import mchorse.bbs.ui.framework.elements.buttons.UIIcon;
import mchorse.bbs.ui.framework.elements.input.list.UILabelList;
import mchorse.bbs.ui.framework.elements.utils.UILabel;
import mchorse.bbs.ui.framework.elements.utils.UIText;
import mchorse.bbs.ui.utils.Area;
import mchorse.bbs.ui.utils.Label;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.ui.utils.UIUtils;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.utils.colors.Colors;
import mchorse.bbs.utils.math.Interpolation;
import mchorse.studio.Studio;
import mchorse.studio.StudioEngine;
import mchorse.studio.settings.StudioSettings;
import mchorse.studio.ui.KeysApp;
import mchorse.studio.ui.UIKeysApp;
import org.lwjgl.opengl.GL11;

public class UIWelcomeMenu extends UIBaseMenu
{
    private static final Link KEYBOARD = Studio.link("textures/keyboard.png");

    private static final int WELCOME = 0;
    private static final int KEYS = 1;
    private static final int FEATURES = 2;
    private static final int RESOURCES = 3;
    private static final int PATRONS = 4;
    private static final int LAST = PATRONS;

    public UILabel title;
    public UIIcon previous;
    public UIIcon next;

    public UIElement keys;
    public UITextureRect keyF1;
    public UITextureRect keyF2;
    public UITextureRect keyF3;
    public UITextureRect keyF6;
    public UITextureRect keyF9;
    public UITextureRect keyF11;
    public UITextureRect keyBackslash;

    public UIElement features;
    public UILabelList<LinkDescription> featureList;

    public UIElement resources;
    public UIButton discord;
    public UIButton wiki;

    public UIText patrons;
    public UIButton patreon;

    private int page;
    private int counter;
    private LinkDescription description;

    public UIWelcomeMenu(IBridge bridge)
    {
        super(bridge);

        this.title = UI.label(IKey.EMPTY).background().labelAnchor(0.5F, 0.5F);
        this.previous = new UIIcon(Icons.MOVE_LEFT, this::previous);
        this.next = new UIIcon(Icons.MOVE_RIGHT, this::next);

        this.title.relative(this.main).x(0.5F).y(20).wh(200, 20).anchorX(0.5F);
        this.previous.relative(this.main).x(0.5F, -100).y(20).anchorX(1F);
        this.next.relative(this.main).x(0.5F, 100).y(20);

        this.keys = new UIElement();
        this.keys.relative(this.main).xy(0.5F, 0.5F).wh(375, 151).anchor(0.5F, 0.5F);

        /* Keys */
        this.keyF1 = new UITextureRect(KEYBOARD, new Area(52, 2, 21, 21), "F1");
        this.keyF1.tooltip(UIKeysApp.WELCOME_KEYS_F1);
        this.keyF2 = new UITextureRect(KEYBOARD, new Area(77, 2, 21, 21), "F2");
        this.keyF2.tooltip(UIKeysApp.WELCOME_KEYS_F2);
        this.keyF3 = new UITextureRect(KEYBOARD, new Area(102, 2, 21, 21), "F3");
        this.keyF3.tooltip(UIKeysApp.WELCOME_KEYS_F3);
        this.keyF6 = new UITextureRect(KEYBOARD, new Area(191, 2, 21, 21), "F6");
        this.keyF6.tooltip(UIKeysApp.WELCOME_KEYS_F6);
        this.keyF9 = new UITextureRect(KEYBOARD, new Area(277, 2, 21, 21), "F9");
        this.keyF9.tooltip(UIKeysApp.WELCOME_KEYS_F9);
        this.keyF11 = new UITextureRect(KEYBOARD, new Area(327, 2, 21, 21), "F11");
        this.keyF11.tooltip(UIKeysApp.WELCOME_KEYS_F11);
        this.keyBackslash = new UITextureRect(KEYBOARD, new Area(297, 82, 21, 21), "\\");
        this.keyBackslash.tooltip(UIKeysApp.WELCOME_KEYS_BACKSLASH);

        this.keys.add(this.keyF1, this.keyF2, this.keyF3, this.keyF6, this.keyF9, this.keyF11, this.keyBackslash);

        for (UITextureRect rect : this.keys.getChildren(UITextureRect.class))
        {
            rect.relative(this.keys).set(rect.rect.x, rect.rect.y, rect.rect.w, rect.rect.h);
        }

        /* Features */
        this.features = new UIElement();
        this.features.relative(this.main).xy(0.5F, 0.5F).wh(400, 212).anchor(0.5F);

        this.featureList = new UILabelList<>((l) -> this.pickDescription(l.get(0)));
        this.featureList.add(new Label<>(
            UIKeysApp.WELCOME_FEATURES_WORLD_EDITOR,
            new LinkDescription(Studio.link("textures/features/world_editor.png"), UIKeysApp.WELCOME_FEATURES_WORLD_EDITOR_DESCRIPTION)
        ));
        this.featureList.add(new Label<>(
            UIKeysApp.WELCOME_FEATURES_TILE_SETS,
            new LinkDescription(Studio.link("textures/features/tile_sets.png"), UIKeysApp.WELCOME_FEATURES_TILE_SETS_DESCRIPTION)
        ));
        this.featureList.add(new Label<>(
            UIKeysApp.WELCOME_FEATURES_PARTICLES,
            new LinkDescription(Studio.link("textures/features/particles.png"), UIKeysApp.WELCOME_FEATURES_PARTICLES_DESCRIPTION)
        ));
        this.featureList.add(new Label<>(
            UIKeysApp.WELCOME_FEATURES_FILM_EDITOR,
            new LinkDescription(Studio.link("textures/features/film_editor.png"), UIKeysApp.WELCOME_FEATURES_FILM_EDITOR_DESCRIPTION)
        ));
        this.featureList.add(new Label<>(
            UIKeysApp.WELCOME_FEATURES_BLOCKBENCH,
            new LinkDescription(Studio.link("textures/features/blockbench.png"), UIKeysApp.WELCOME_FEATURES_BLOCKBENCH_DESCRIPTION)
        ));
        this.featureList.background(Colors.A25).sort();
        this.featureList.add(new Label<>(
            UIKeysApp.WELCOME_FEATURES_MORE,
            new LinkDescription(Studio.link("textures/features/more.png"), UIKeysApp.WELCOME_FEATURES_MORE_DESCRIPTION)
        ));
        this.featureList.relative(this.features).w(120).h(1F);

        this.features.add(this.featureList);

        /* External resources */
        this.discord = new UIButton(UIKeysApp.WELCOME_DISCORD, (b) -> UIUtils.openWebLink("https://discord.gg/N7ZZyNd4UC"));
        this.wiki = new UIButton(UIKeysApp.WELCOME_WIKI, (b) -> UIUtils.openWebLink("https://github.com/BBS-Engine/bbs/wiki"));
        this.resources = UI.row(this.wiki, this.discord);
        this.resources.relative(this.main).x(0.5F).y(0.5F, 75).wh(300, 20).anchorX(0.5F);

        /* Patrons */
        this.patreon = new UIButton(UIKeysApp.WELCOME_PATREON, (b) -> UIUtils.openWebLink("https://www.patreon.com/McHorse"));
        this.patreon.relative(this.main).x(0.5F).y(0.5F, 75).wh(300, 20).anchorX(0.5F);

        this.patrons = new UIText();
        this.patrons.text(UIKeysApp.WELCOME_PATRONS.format(IKey.raw("Дмитрий Танасийчук\nEgor\niSilent3\nWilheries")));
        this.patrons.lineHeight(14);
        this.patrons.relative(this.patreon).y(-10).w(1F).anchor(0, 1F);

        this.main.add(this.title, this.previous, this.next);
        this.main.add(this.keys, this.features, this.resources);
        this.main.add(this.patreon, this.patrons);

        this.switchToPage(WELCOME);

        this.main.keys().register(KeysApp.PREV_PAGE, () -> this.switchToPage(this.page - 1)).active(() -> this.page > 0);
        this.main.keys().register(KeysApp.NEXT_PAGE, () -> this.switchToPage(this.page + 1)).active(() -> this.page < LAST);
    }

    @Override
    public Link getMenuId()
    {
        return Studio.link("welcome");
    }

    @Override
    public boolean canPause()
    {
        return false;
    }

    private void previous(UIIcon b)
    {
        this.switchToPage(this.page - 1);
    }

    private void next(UIIcon b)
    {
        if (this.page < LAST)
        {
            this.switchToPage(this.page + 1);
        }
        else
        {
            StudioSettings.welcome.set(true);

            this.closeMenu();
        }
    }

    private void pickDescription(Label<LinkDescription> description)
    {
        this.description = description.value;
    }

    public void switchToPage(int page)
    {
        this.page = page;

        this.previous.setVisible(page != WELCOME);
        this.next.both(page == LAST ? Icons.CLOSE : Icons.MOVE_RIGHT);
        this.title.setVisible(page != WELCOME);

        this.keys.setVisible(page == KEYS);
        this.features.setVisible(page == FEATURES);
        this.resources.setVisible(page == RESOURCES);

        this.patrons.setVisible(page == PATRONS);
        this.patreon.setVisible(page == PATRONS);

        if (this.page == WELCOME)
        {
            this.counter = 0;
        }
        else if (this.page == KEYS)
        {
            this.title.label = UIKeysApp.WELCOME_SECTIONS_KEYS;
        }
        else if (this.page == FEATURES)
        {
            this.title.label = UIKeysApp.WELCOME_SECTIONS_FEATURES;
            this.featureList.setIndex(0);
            this.featureList.scroll.scrollTo(0);
            this.pickDescription(this.featureList.getCurrentFirst());
        }
        else if (this.page == RESOURCES)
        {
            this.title.label = UIKeysApp.WELCOME_SECTIONS_RESOURCES;
        }
        else if (this.page == PATRONS)
        {
            this.title.label = UIKeysApp.WELCOME_SECTIONS_PATRONS;
        }
    }

    @Override
    protected void closeMenu()
    {
        if (this.page == LAST)
        {
            StudioEngine engine = (StudioEngine) this.bridge.getEngine();

            engine.screen.showMenu(engine.screen.getDashboard());
        }
    }

    @Override
    public void update()
    {
        super.update();

        this.counter += 1;
    }

    @Override
    protected void preRenderMenu(UIRenderingContext context)
    {
        this.renderDefaultBackground();

        FontRenderer font = context.getFont();
        int x = this.width / 2;
        int y = this.height / 2;

        this.renderDropArea(this.next.area);

        if (this.previous.isVisible())
        {
            this.renderDropArea(this.previous.area);
        }

        if (this.page == WELCOME)
        {
            String label = UIKeysApp.WELCOME_WELCOME_TITLE.get();
            float factor = Math.min((this.counter + context.getTransition()) / 25F, 1);
            float scale = Interpolation.EXP_OUT.interpolate(20, 4, factor);

            context.stack.push();
            context.stack.translate(x, y, 0);
            context.stack.scale(scale, scale, 1);

            context.batcher.textShadow(font, label, -font.getWidth(label) / 2, -4);

            context.stack.pop();

            String subtext = UIKeysApp.WELCOME_WELCOME_SUBTITLE.get();
            int ly = y + 2 * font.getHeight() + 16;

            ly = (int) Interpolation.EXP_OUT.interpolate(this.height * 1.5F, ly, factor);

            for (String line : font.split(subtext, 320))
            {
                context.batcher.textShadow(font, line, x - font.getWidth(line) / 2, ly);

                ly += font.getHeight() * 2;
            }
        }
        else if (this.page == KEYS)
        {
            Texture texture = context.getTextures().getTexture(KEYBOARD);
            String label = UIKeysApp.WELCOME_KEYS_SUBTITLE.get();
            int tx = this.keys.area.x;
            int ty = this.keys.area.y;

            context.batcher.texturedBox(texture, Colors.GRAY, tx, ty, texture.width, texture.height, 0, 0);
            context.batcher.textShadow(font, label, x - font.getWidth(label) / 2, y + texture.height / 2 + 8);
        }
        else if (this.page == FEATURES)
        {
            if (this.description != null)
            {
                Texture texture = context.getTextures().getTexture(this.description.link, GL11.GL_LINEAR);

                int ex = this.featureList.area.ex();
                int ey = this.featureList.area.y;

                this.features.area.render(context.batcher, Colors.A50);

                context.batcher.texturedBox(texture, Colors.WHITE, ex, ey, 280, 140, 0, 0, 560, 280, 560, 280);

                int lx = ex + 8;
                int ly = ey + 140 + 8;

                for (String line : font.split(this.description.description.get(), this.features.area.w - this.featureList.area.w - 16))
                {
                    context.batcher.textShadow(font, line, lx, ly);

                    ly += 12;
                }
            }
        }
        else if (this.page == RESOURCES)
        {
            String label = UIKeysApp.WELCOME_RESOURCES_TITLE.get();
            float scale = 3;

            context.stack.push();
            context.stack.translate(x, y, 0);
            context.stack.scale(scale, scale, 1);

            context.batcher.textShadow(font, label, -font.getWidth(label) / 2, -4);

            context.stack.pop();

            String subtext = UIKeysApp.WELCOME_RESOURCES_SUBTITLE.get();
            int ly = y + 2 * font.getHeight() + 12;

            for (String line : font.split(subtext, 320))
            {
                context.batcher.textShadow(font, line, x - font.getWidth(line) / 2, ly);

                ly += font.getHeight() * 2;
            }

            String tip = UIKeysApp.WELCOME_RESOURCES_TIP.get();

            context.batcher.textCard(font, tip, this.resources.area.mx(font.getWidth(tip)), this.resources.area.y + 26);
        }
        else if (this.page == PATRONS)
        {
            String label = UIKeysApp.WELCOME_THANK_YOU.get();
            float scale = 4;

            context.stack.push();
            context.stack.translate(x, this.patrons.area.y - font.getHeight() * 3, 0);
            context.stack.scale(scale, scale, 1);

            context.batcher.textShadow(font, label, -font.getWidth(label) / 2, -font.getHeight());

            context.stack.pop();
        }
    }

    private void renderDropArea(Area area)
    {
        boolean hover = area.isInside(this.context);

        int opaque = hover ? Colors.A50 | BBSSettings.primaryColor.get() : Colors.A50;
        int shadow = hover ? BBSSettings.primaryColor.get() : 0;

        this.context.batcher.dropShadow(area.x + 4, area.y + 4, area.ex() - 4, area.ey() - 4, 4, opaque, shadow);
    }

    public static class LinkDescription
    {
        public Link link;
        public IKey description;

        public LinkDescription(Link link, IKey description)
        {
            this.link = link;
            this.description = description;
        }
    }
}