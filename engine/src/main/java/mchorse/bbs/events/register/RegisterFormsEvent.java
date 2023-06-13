package mchorse.bbs.events.register;

import mchorse.bbs.forms.FormArchitect;

public class RegisterFormsEvent
{
    public FormArchitect forms;

    public RegisterFormsEvent(FormArchitect forms)
    {
        this.forms = forms;
    }
}