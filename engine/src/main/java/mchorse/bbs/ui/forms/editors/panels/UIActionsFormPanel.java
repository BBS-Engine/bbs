package mchorse.bbs.ui.forms.editors.panels;

import mchorse.bbs.cubic.animation.ActionConfig;
import mchorse.bbs.cubic.animation.ActionsConfig;
import mchorse.bbs.forms.forms.ModelForm;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.forms.editors.forms.UIForm;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.buttons.UIToggle;
import mchorse.bbs.ui.framework.elements.input.UITrackpad;
import mchorse.bbs.ui.framework.elements.input.list.UISearchList;
import mchorse.bbs.ui.framework.elements.input.list.UIStringList;
import mchorse.bbs.ui.utils.UI;

public class UIActionsFormPanel extends UIFormPanel<ModelForm>
{
    public UIStringList actions;

    public UISearchList<String> pickAction;
    public UIToggle loop;
    public UITrackpad speed;
    public UITrackpad fade;
    public UITrackpad tick;

    private ActionConfig action;

    public UIActionsFormPanel(UIForm editor)
    {
        super(editor);

        this.actions = new UIStringList((l) -> this.pickAction(l.get(0), false));
        this.actions.background().relative(this).x(1F, -10).y(22).w(this.options.getFlex().getW() - 20).h(1F, -32).anchorX(1F);

        this.pickAction = new UISearchList<>(new UIStringList((l) ->
        {
            this.action.name = l.get(0);
            this.form.resetAnimator();
        }));
        this.pickAction.label(UIKeys.SEARCH).list.background();
        this.pickAction.h(132);
        this.loop = new UIToggle(UIKeys.FORMS_EDITORS_ACTIONS_LOOPS, (b) ->
        {
            this.action.loop = b.getValue();
            this.form.resetAnimator();
        });
        this.speed = new UITrackpad((v) ->
        {
            this.action.speed = v.floatValue();
            this.form.resetAnimator();
        });
        this.fade = new UITrackpad((v) ->
        {
            this.action.fade = v.floatValue();
            this.form.resetAnimator();
        });
        this.fade.limit(0);
        this.tick = new UITrackpad((v) ->
        {
            this.action.tick = v.intValue();
            this.form.resetAnimator();
        });
        this.tick.limit(0).integer();

        this.options.add(this.loop, UI.label(UIKeys.FORMS_EDITORS_ACTIONS_ACTION).marginTop(8), this.pickAction);
        this.options.add(UI.label(UIKeys.FORMS_EDITORS_ACTIONS_SPEED).marginTop(8), this.speed);
        this.options.add(UI.label(UIKeys.FORMS_EDITORS_ACTIONS_FADE).marginTop(8), this.fade);
        this.options.add(UI.label(UIKeys.FORMS_EDITORS_ACTIONS_TICK).marginTop(8), this.tick);

        this.add(this.actions);
    }

    private void pickAction(String key, boolean select)
    {
        ActionsConfig config = this.form.actions.get();

        this.action = config.actions.get(key);

        if (this.action == null)
        {
            this.action = new ActionConfig(key);

            config.actions.put(key, this.action);
        }

        this.pickAction.list.setCurrentScroll(this.action.name);
        this.loop.setValue(this.action.loop);
        this.speed.setValue(this.action.speed);
        this.fade.setValue(this.action.fade);
        this.tick.setValue(this.action.tick);

        if (select)
        {
            this.actions.setCurrentScroll(key);
        }
    }

    @Override
    public void startEdit(ModelForm form)
    {
        super.startEdit(form);

        this.form.ensureAnimator();

        this.pickAction.list.clear();
        this.pickAction.list.add(this.form.getModel().animations.animations.keySet());
        this.pickAction.list.sort();

        this.actions.clear();
        this.actions.add(this.form.getAnimator().getActions());
        this.actions.sort();

        this.pickAction("idle", true);
    }

    @Override
    public void finishEdit()
    {
        super.finishEdit();

        ActionsConfig.removeDefaultActions(this.form.actions.get().actions);
    }

    @Override
    public void render(UIContext context)
    {
        super.render(context);

        /* TODO: Extract */
        context.batcher.textShadow("Actions", this.actions.area.x, this.actions.area.y - 12);
    }
}