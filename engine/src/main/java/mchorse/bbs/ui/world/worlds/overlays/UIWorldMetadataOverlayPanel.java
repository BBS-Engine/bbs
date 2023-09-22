package mchorse.bbs.ui.world.worlds.overlays;

import mchorse.bbs.BBS;
import mchorse.bbs.BBSSettings;
import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.resources.Link;
import mchorse.bbs.settings.values.base.BaseValue;
import mchorse.bbs.settings.values.base.IValueUIProvider;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.UIScrollView;
import mchorse.bbs.ui.framework.elements.buttons.UIButton;
import mchorse.bbs.ui.framework.elements.buttons.UICirculate;
import mchorse.bbs.ui.framework.elements.buttons.UIToggle;
import mchorse.bbs.ui.framework.elements.input.UITrackpad;
import mchorse.bbs.ui.framework.elements.input.text.UITextbox;
import mchorse.bbs.ui.framework.elements.overlay.UIConfirmOverlayPanel;
import mchorse.bbs.ui.framework.elements.overlay.UIMessageOverlayPanel;
import mchorse.bbs.ui.framework.elements.overlay.UIOverlay;
import mchorse.bbs.ui.framework.elements.overlay.UIOverlayPanel;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.ui.world.worlds.UIRange;
import mchorse.bbs.utils.colors.Colors;
import mchorse.bbs.voxel.generation.Generator;
import mchorse.bbs.voxel.tilesets.BlockSet;
import mchorse.bbs.world.WorldMetadata;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class UIWorldMetadataOverlayPanel extends UIOverlayPanel
{
    public UIScrollView view;

    public UITextbox id;
    public UITextbox name;

    public UITrackpad seed;
    public UICirculate generator;

    public UIElement generatorOptions;

    public UITrackpad chunks;
    public UITrackpad chunkSize;
    public UIToggle compress;
    public UIToggle column;

    public UITrackpad columnBase;
    public UITrackpad columnHeight;

    public UIRange x;
    public UIRange y;
    public UIRange z;

    public UIButton submit;

    private BlockSet blocks;
    private Consumer<UIWorldMetadataOverlayPanel> callback;
    private Set<String> existing;
    private List<Link> generators;
    private WorldMetadata metadata;
    private Generator gen;

    private boolean iDontCare;

    public UIWorldMetadataOverlayPanel(BlockSet blocks, Consumer<UIWorldMetadataOverlayPanel> callback, Set<String> existing)
    {
        super(UIKeys.WORLDS_OPTIONS_TITLE);

        this.blocks = blocks;
        this.callback = callback;
        this.existing = existing;

        this.generators = new ArrayList<>(BBS.getFactoryGenerators().getKeys());

        this.view = UI.scrollView(5, 10);
        this.view.markContainer();

        this.id = new UITextbox(this::verifyId);
        this.name = new UITextbox((s) -> this.metadata.name = s);

        this.seed = new UITrackpad((v) -> this.metadata.seed = v.longValue());
        this.generator = new UICirculate((b) ->
        {
            this.metadata.generator = this.getGenerator(b.getValue());

            this.rebuildGeneratorSpecificOptions();
            this.resize();
        });

        for (Link generator : this.generators)
        {
            this.generator.addLabel(IKey.raw(generator.toString()));
        }

        this.generatorOptions = UI.column().marginBottom(12);

        this.chunks = new UITrackpad((v) -> this.metadata.chunks = v.intValue()).limit(1, 256, true);
        this.chunkSize = new UITrackpad((v) -> this.metadata.chunkSize = v.intValue()).limit(2, 128, true);
        this.compress = new UIToggle(UIKeys.WORLDS_OPTIONS_COMPRESS, (b) -> this.metadata.compress = b.getValue());
        this.column = new UIToggle(UIKeys.WORLDS_OPTIONS_STORAGE, (b) ->
        {
            this.metadata.column = b.getValue();

            this.rebuild();
        });

        this.columnBase = new UITrackpad((v) -> this.metadata.columnBase = v.intValue()).integer();
        this.columnHeight = new UITrackpad((v) -> this.metadata.columnHeight = v.intValue()).limit(0);

        this.x = new UIRange(UIKeys.WORLDS_OPTIONS_LIMIT_X);
        this.y = new UIRange(UIKeys.WORLDS_OPTIONS_LIMIT_Y);
        this.z = new UIRange(UIKeys.WORLDS_OPTIONS_LIMIT_Z);

        this.submit = new UIButton(UIKeys.WORLDS_OPTIONS_CREATE, this::submit);

        /* Layout */
        this.view.relative(this.content).full();

        this.content.add(this.view);

        this.rebuild();
    }

    public WorldMetadata getMetadata()
    {
        return this.metadata;
    }

    public void setMetadata(WorldMetadata metadata)
    {
        this.metadata = metadata;

        if (this.metadata == null)
        {
            return;
        }

        this.name.setText(metadata.name);
        this.seed.setValue(metadata.seed);
        this.generator.setValue(this.getGenerator(metadata.generator));
        this.chunks.setValue(metadata.chunks);
        this.chunkSize.setValue(metadata.chunkSize);
        this.compress.setValue(metadata.compress);
        this.column.setValue(metadata.column);
        this.columnBase.setValue(metadata.columnBase);
        this.columnHeight.setValue(metadata.columnHeight);
        this.x.setRange(metadata.limitX);
        this.y.setRange(metadata.limitY);
        this.z.setRange(metadata.limitZ);

        this.rebuild();
    }

    private Link getGenerator(int value)
    {
        if (value >= 0 && value < this.generators.size())
        {
            return this.generators.get(value);
        }

        return Generator.DEFAULT;
    }

    private int getGenerator(Link value)
    {
        for (int i = 0; i < this.generators.size(); i++)
        {
            if (this.generators.get(i).equals(value.toString()))
            {
                return i;
            }
        }

        return 0;
    }

    private void verifyId(String world)
    {
        this.id.setColor(this.existing.contains(world) ? Colors.NEGATIVE : Colors.WHITE);
    }

    protected void rebuild()
    {
        int color = Colors.A50 | BBSSettings.primaryColor.get();

        this.view.removeAll();
        this.view.add(UI.label(UIKeys.WORLDS_OPTIONS_ID).background(color), this.id.marginBottom(12));
        this.view.add(UI.label(UIKeys.WORLDS_OPTIONS_TITLE).background(color), this.name.marginBottom(12));
        this.view.add(UI.label(UIKeys.WORLDS_OPTIONS_GENERATOR_OPTIONS).background(color));
        this.view.add(UI.label(UIKeys.WORLDS_OPTIONS_SEED).marginTop(6), this.seed, this.generator.marginBottom(12));

        if (this.metadata != null)
        {
            this.view.add(this.generatorOptions);

            this.rebuildGeneratorSpecificOptions();
        }

        this.view.add(UI.label(UIKeys.WORLDS_OPTIONS_GENERAL).background(color));
        this.view.add(UI.label(UIKeys.WORLDS_OPTIONS_CHUNKS).marginTop(6), this.chunks);
        this.view.add(UI.label(UIKeys.WORLDS_OPTIONS_CHUNK_SIZE).marginTop(6), this.chunkSize);
        this.view.add(this.compress, this.column.marginBottom(12));

        if (this.column.getValue())
        {
            this.view.add(UI.label(UIKeys.WORLDS_OPTIONS_COLUMN_OPTIONS).background(color));
            this.view.add(UI.row(UI.label(UIKeys.WORLDS_OPTIONS_COLUMN_BASE, 20).labelAnchor(0, 0.5F), this.columnBase));
            this.view.add(UI.row(UI.label(UIKeys.WORLDS_OPTIONS_COLUMN_HEIGHT, 20).labelAnchor(0, 0.5F), this.columnHeight).marginBottom(12));
        }

        this.view.add(UI.label(UIKeys.WORLDS_OPTIONS_GENERATION_LIMIT).background(color).marginBottom(6), this.x.marginBottom(6));

        if (!this.column.getValue())
        {
            this.view.add(this.y.marginBottom(6));
        }

        this.view.add(this.z.marginBottom(6));
        this.view.add(this.submit);

        this.view.resize();
    }

    protected void rebuildGeneratorSpecificOptions()
    {
        int color = Colors.A50 | BBSSettings.primaryColor.get();

        Generator generator = Generator.forName(this.getGenerator(this.generator.getValue()));
        List<BaseValue> generatorOptions = generator.getValues();

        this.gen = generator;
        this.generatorOptions.removeAll();

        if (!generatorOptions.isEmpty())
        {
            this.generatorOptions.add(UI.label(UIKeys.WORLDS_OPTIONS_GENERATOR_OPTIONS).background(color));

            for (BaseValue value : generatorOptions)
            {
                if (value instanceof IValueUIProvider)
                {
                    for (UIElement element : ((IValueUIProvider) value).getFields(this))
                    {
                        this.generatorOptions.add(element);
                    }
                }
            }
        }
    }

    private void submit(UIButton b)
    {
        if (this.callback == null)
        {
            return;
        }

        if (this.cannotSubmitWorldId())
        {
            return;
        }

        if (this.gen != null)
        {
            for (BaseValue value : this.gen.getValues())
            {
                this.metadata.metadata.put(value.getPath(), value.toData());
            }

            if (this.cannotSubmitWithEmptyGeneratorValues())
            {
                return;
            }
        }

        this.callback.accept(this);
        this.close();
    }

    protected boolean cannotSubmitWorldId()
    {
        if (this.id.getText().trim().isEmpty())
        {
            UIOverlay.addOverlay(this.getContext(), new UIMessageOverlayPanel(
                UIKeys.GENERAL_WARNING,
                UIKeys.WORLDS_OPTIONS_WARNING_WORLD_ID
            ));

            return true;
        }

        return false;
    }

    protected boolean cannotSubmitWithEmptyGeneratorValues()
    {
        if (this.isMetadataEmpty(this.metadata.metadata) && !this.iDontCare)
        {
            UIOverlay.addOverlay(this.getContext(), new UIConfirmOverlayPanel(
                UIKeys.GENERAL_WARNING,
                UIKeys.WORLDS_OPTIONS_WARNING_EMPTY_DATA,
                (confirm) -> this.iDontCare = confirm
            ));

            return true;
        }

        return false;
    }

    private boolean isMetadataEmpty(MapType data)
    {
        for (String key : data.keys())
        {
            BaseType type = data.get(key);

            if (type.isString() && !type.asString().trim().isEmpty())
            {
                return false;
            }
            else if (type.isList() && !type.asList().isEmpty())
            {
                return false;
            }
            else if (type.isMap() && !type.asMap().isEmpty())
            {
                return false;
            }
        }

        return true;
    }
}