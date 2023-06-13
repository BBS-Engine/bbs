package mchorse.bbs.events;

import mchorse.bbs.l10n.L10n;

public class L10nReloadEvent
{
    public final L10n l10n;

    public L10nReloadEvent(L10n l10n)
    {
        this.l10n = l10n;
    }
}