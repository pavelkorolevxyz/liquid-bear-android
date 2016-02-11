package com.pillowapps.liqear.helpers;

import com.pillowapps.liqear.entities.Album;
import com.pillowapps.liqear.entities.Artist;
import com.pillowapps.liqear.entities.Group;
import com.pillowapps.liqear.entities.Tag;
import com.pillowapps.liqear.entities.Track;
import com.pillowapps.liqear.entities.User;
import com.pillowapps.liqear.entities.lastfm.LastfmAlbum;
import com.pillowapps.liqear.entities.lastfm.LastfmAlbumWithName;
import com.pillowapps.liqear.entities.lastfm.LastfmArtist;
import com.pillowapps.liqear.entities.lastfm.LastfmArtistStruct;
import com.pillowapps.liqear.entities.lastfm.LastfmImage;
import com.pillowapps.liqear.entities.lastfm.LastfmTag;
import com.pillowapps.liqear.entities.lastfm.LastfmTopAlbum;
import com.pillowapps.liqear.entities.lastfm.LastfmTrack;
import com.pillowapps.liqear.entities.lastfm.LastfmTrackArtistStruct;
import com.pillowapps.liqear.entities.lastfm.LastfmUser;
import com.pillowapps.liqear.entities.setlistfm.SetlistfmTrack;
import com.pillowapps.liqear.entities.vk.VkAlbum;
import com.pillowapps.liqear.entities.vk.VkGroup;
import com.pillowapps.liqear.entities.vk.VkTrack;
import com.pillowapps.liqear.entities.vk.VkUser;

import java.util.ArrayList;
import java.util.List;

// todo replace with mapper
public class Converter {

    private Converter() {
        // no-op
    }

    public static Track convertTrack(LastfmTrack lastfmTrack) {
        String artist = lastfmTrack.getArtist().getName();
        String title = lastfmTrack.getName();
        Track track = new Track(artist, title);
        track.setLoved(lastfmTrack.isLoved());
        return track;
    }

    public static Track convertVkTrack(VkTrack vkTrack) {
        String artist = vkTrack.getArtist();
        String title = vkTrack.getTitle();
        Track track = new Track(artist, title);
        track.setAudioId(vkTrack.getAudioId());
        track.setOwnerId(vkTrack.getOwnerId());
        return track;
    }

    public static Artist convertArtist(LastfmArtist lastfmArtist) {
        String artistName = lastfmArtist.getName();
        List<LastfmImage> images = lastfmArtist.getImages();
        Artist artist = new Artist(artistName);
        if (images != null) {
            LastfmImage lastImage = images.get(images.size() - 1);
            if (lastImage.getSize().isEmpty() && images.size() > 1) {
                lastImage = images.get(images.size() - 2);
            }
            String previewUrl = lastImage.getUrl();
            artist.setImageUrl(previewUrl);
        }
        return artist;
    }

    public static User convertUser(LastfmUser lastfmUser) {
        String name = lastfmUser.getName();
        User user = new User(name);
        List<LastfmImage> images = lastfmUser.getImages();
        if (images != null) {
            LastfmImage lastImage = images.get(images.size() - 1);
            if (lastImage.getSize().isEmpty() && images.size() > 1) {
                lastImage = images.get(images.size() - 2);
            }
            user.setImageUrl(lastImage.getUrl());
        }
        return user;
    }

    private static Tag convertTag(LastfmTag lastfmTag) {
        String name = lastfmTag.getName();
        return new Tag(name);
    }

    public static Album convertAlbum(LastfmAlbum lastfmAlbum) {
        if (lastfmAlbum == null) return null;
        String artist = lastfmAlbum.getArtistName();
        String name = lastfmAlbum.getTitle();

        Album album = new Album(artist, name);
        List<LastfmImage> images = lastfmAlbum.getImages();
        String imageUrl = null;
        if (images != null && images.size() > 0) {
            LastfmImage lastImage = images.get(images.size() - 1);
            if (lastImage.getSize().isEmpty() && images.size() > 1) {
                lastImage = images.get(images.size() - 2);
            }
            imageUrl = lastImage.getUrl();
        }
        album.setImageUrl(imageUrl);
        return album;
    }

    public static Album convertAlbum(LastfmAlbumWithName lastfmAlbum) {
        if (lastfmAlbum == null) return null;
        String artist = lastfmAlbum.getArtistName();
        String name = lastfmAlbum.getTitle();

        Album album = new Album(artist, name);
        List<LastfmImage> images = lastfmAlbum.getImages();
        String imageUrl = null;
        if (images != null && images.size() > 0) {
            LastfmImage lastImage = images.get(images.size() - 1);
            if (lastImage.getSize().isEmpty() && images.size() > 1) {
                lastImage = images.get(images.size() - 2);
            }
            imageUrl = lastImage.getUrl();
        }
        album.setImageUrl(imageUrl);
        return album;
    }

    public static List<Track> convertLastfmTrackList(List<LastfmTrack> lastfmTracks) {
        List<Track> tracks = new ArrayList<>();
        for (LastfmTrack lastfmTrack : lastfmTracks) {
            Track track = convertTrack(lastfmTrack);
            tracks.add(track);
        }
        return tracks;
    }

    public static List<Track> convertVkTrackList(List<VkTrack> vkTracks) {
        List<Track> tracks = new ArrayList<>();
        for (VkTrack lastfmTrack : vkTracks) {
            Track track = convertVkTrack(lastfmTrack);
            tracks.add(track);
        }
        return tracks;
    }

    public static List<Artist> convertArtistList(List<LastfmArtist> lastfmArtists) {
        List<Artist> artists = new ArrayList<>();
        for (LastfmArtist lastfmArtist : lastfmArtists) {
            Artist artist = convertArtist(lastfmArtist);
            artists.add(artist);
        }
        return artists;
    }

    public static List<Tag> convertTags(List<LastfmTag> lastfmTags) {
        List<Tag> tags = new ArrayList<>();
        for (LastfmTag lastfmTag : lastfmTags) {
            Tag tag = convertTag(lastfmTag);
            tags.add(tag);
        }
        return tags;
    }

    public static List<Album> convertAlbums(List<LastfmAlbum> lastfmAlbums) {
        List<Album> albums = new ArrayList<>();
        for (LastfmAlbum lastfmAlbum : lastfmAlbums) {
            Album album = convertAlbum(lastfmAlbum);
            albums.add(album);
        }
        return albums;
    }

    public static List<User> convertUsers(List<LastfmUser> lastfmUsers) {
        List<User> users = new ArrayList<>();
        for (LastfmUser lastfmUser : lastfmUsers) {
            User user = convertUser(lastfmUser);
            users.add(user);
        }
        return users;
    }

    public static List<Group> convertGroups(List<VkGroup> vkGroups) {
        List<Group> groups = new ArrayList<>();
        for (VkGroup group : vkGroups) {
            Group user = convertGroup(group);
            groups.add(user);
        }
        return groups;
    }

    private static Group convertGroup(VkGroup vkGroup) {
        Group group = new Group();
        group.setGid(vkGroup.getId());
        group.setImageUrl(vkGroup.getImageMedium());
        group.setName(vkGroup.getName());
        return group;
    }

    public static List<User> convertVkUserList(List<VkUser> vkUsers) {
        List<User> users = new ArrayList<>();
        for (VkUser vkUser : vkUsers) {
            User user = convertUser(vkUser);
            users.add(user);
        }
        return users;
    }

    private static User convertUser(VkUser vkUser) {
        User user = new User(vkUser.getName());
        user.setUid(vkUser.getId());
        user.setImageUrl(vkUser.getPhotoMedium());
        return user;
    }

    public static LastfmArtist convertLastfmArtistStruct(LastfmArtistStruct artistStruct) {
        LastfmArtist artist = new LastfmArtist();
        artist.setName(artistStruct.getName());
        return artist;
    }

    public static List<LastfmTrack> convertLastfmTracksArtistStruct(List<LastfmTrackArtistStruct> tracksArtistStruct) {
        List<LastfmTrack> lastfmTracks = new ArrayList<>();
        for (LastfmTrackArtistStruct trackToConvert : tracksArtistStruct) {
            LastfmTrack track = new LastfmTrack();
            track.setName(trackToConvert.getName());
            track.setArtist(convertLastfmArtistStruct(trackToConvert.getArtist()));
            lastfmTracks.add(track);
        }
        return lastfmTracks;
    }

    public static List<Track> convertSetlistTracks(String artist, List<SetlistfmTrack> setlistfmTracks) {
        List<Track> tracks = new ArrayList<>();
        if (setlistfmTracks == null) return tracks;
        for (SetlistfmTrack setlistfmTrack : setlistfmTracks) {
            tracks.add(convertTrack(artist, setlistfmTrack));
        }
        return tracks;
    }

    private static Track convertTrack(String artist, SetlistfmTrack setlistfmTrack) {
        return new Track(artist, setlistfmTrack.getTitle());
    }

    public static List<LastfmAlbum> convertTopAlbums(List<LastfmTopAlbum> topAlbums) {
        List<LastfmAlbum> lastfmAlbums = new ArrayList<>();
        if (topAlbums == null) return lastfmAlbums;
        for (LastfmTopAlbum topAlbum : topAlbums) {
            lastfmAlbums.add(convertTopAlbum(topAlbum));
        }
        return lastfmAlbums;
    }

    public static LastfmAlbum convertTopAlbum(LastfmTopAlbum topAlbum) {
        LastfmAlbum lastfmAlbum = new LastfmAlbum();
        lastfmAlbum.setArtistName(topAlbum.getArtist().getName());
        lastfmAlbum.setTitle(topAlbum.getName());
        lastfmAlbum.setTracks(topAlbum.getTracks());
        lastfmAlbum.setImages(topAlbum.getImages());
        lastfmAlbum.setReleaseDate(topAlbum.getReleaseDate());
        return lastfmAlbum;
    }

    public static Album convertVkAlbum(VkAlbum vkAlbum) {
        Album album = new Album();
        album.setTitle(vkAlbum.getTitle());
        album.setOwnerId(vkAlbum.getOwnerId());
        album.setAlbumId(vkAlbum.getAlbumId());
        return album;
    }

    public static List<Album> convertVkAlbums(List<VkAlbum> vkAlbums) {
        List<Album> albums = new ArrayList<>();
        if (vkAlbums == null) return albums;
        for (VkAlbum topAlbum : vkAlbums) {
            albums.add(convertVkAlbum(topAlbum));
        }
        return albums;
    }
}
