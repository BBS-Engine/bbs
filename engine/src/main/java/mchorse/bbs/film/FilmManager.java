package mchorse.bbs.film;

import mchorse.bbs.data.types.MapType;
import mchorse.bbs.utils.manager.BaseManager;
import mchorse.bbs.utils.manager.storage.CompressedDataStorage;

import java.io.File;

public class FilmManager extends BaseManager<Film>
{
    public FilmManager(File folder)
    {
        super(folder);

        this.storage = new CompressedDataStorage();
    }

    @Override
    protected Film createData(String id, MapType mapType)
    {
        Film film = new Film();

        if (mapType != null)
        {
            film.fromData(mapType);
        }

        return film;
    }
}