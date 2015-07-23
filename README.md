Liquid Bear
==========

Online music player for Android

<a href="https://play.google.com/store/apps/details?id=com.pillowapps.liqear">
  <img alt="Get it on Google Play"
       src="https://developer.android.com/images/brand/en_generic_rgb_wo_60.png" />
</a>

#How to make your own Liquid Bear
You need to create applications in Vk and Last.fm developer consoles.

Add these stored procedures to VK application:

###execute.getLyrics
```javascript
var q = Args.notation;
var index = Args.index;
var count = index + 1;
var searchResults = API.audio.search({"q":q,"sort":2, "count":count, "lyrics":1});
var first_elem = searchResults.items[0];
var last_elem = searchResults.items[index];
var searchLyricsResults = API.audio.getLyrics({"lyrics_id":first_elem.lyrics_id});
var searchLyricsResults2 = API.audio.getLyrics({"lyrics_id":last_elem.lyrics_id});
return [searchLyricsResults, searchLyricsResults2];
```

###execute.getUrl
```javascript
var q = Args.notation;
var index = Args.index;
var count = index + 1;
var searchResults = API.audio.search({"q":q,"sort":2, "count":count});
return [searchResults.items[0], searchResults.items[index]];
```

###execute.getUrlById
```javascript
var aid = Args.audioId;
var oid = Args.ownerId;
var q = Args.notation;
var index = Args.index;
var count = index + 1;
var idString = oid + "_" + aid;
var trackById = API.audio.getById({"audios":idString});
var searchResults = API.audio.search({"q":q,"sort":2, "count":count});
return [trackById[0], searchResults.items[0], searchResults.items[index]];
```

###execute.searchAndPostStatus
```javascript
var q = Args.notation;
var index = Args.index;
var count = index + 1;
var searchResults = API.audio.search({"q":q,"sort":1, "count":count});
var sR = searchResults[1];
var audioId = sR.oid+"_"+sR.aid;
return API.status.set({"audio":audioId});
```
