package com.example.youtubeclonezti;

import com.example.youtubeclonezti.models.Playlist;
import com.example.youtubeclonezti.repositories.PlaylistRepository;
import org.bson.types.ObjectId;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "/application.properties")
public class PlaylistControllerTests {

    @Autowired
    private PlaylistRepository playlistRepository;

    @Autowired
    org.springframework.data.mongodb.core.MongoTemplate mongoTemplate;


    @Before
    public void setup() throws Exception {
        mongoTemplate.remove(new Query(), Playlist.class);
    }

    @After
    public void tearDown() throws Exception {
        mongoTemplate.remove(new Query(), Playlist.class);
    }

    @Test
    public void testSaveAndFindPlaylistByTitle() throws Exception {
        Playlist playlist1 = new Playlist( "one");
        Playlist playlist2 = new Playlist( "two");
        playlist1.setId(new ObjectId("ae9ace903d59322d1b45a743"));
        playlist2.setId(new ObjectId("be9ace903d59322d1b45a743"));

        playlistRepository.save(playlist1);
        playlistRepository.save(playlist2);


        assertEquals(playlist1, playlistRepository.findPlaylistById("ae9ace903d59322d1b45a743"));
        assertEquals(playlist2, playlistRepository.findPlaylistById("be9ace903d59322d1b45a743"));

        assertNull(playlistRepository.findPlaylistById("ce9ace903d59322d1b45a743"));
    }

    @Test
    public void testSortPlaylistsByTitleDescendingAndAllPublicAndActiveAndAnonymousUser() throws Exception {
        Playlist playlist1 = new Playlist( "one");
        Playlist playlist2 = new Playlist( "two");

        playlistRepository.save(playlist1);
        playlistRepository.save(playlist2);

        Sort sort = Sort.by(Sort.Direction.DESC, "title");
        List<Playlist> playlists = playlistRepository.findAllByIsActiveTrueAndIsPublicTrue(sort);

        assertEquals(playlists.toString(), "[two, one]");
    }

    @Test
    public void testSortPlaylistsByTitleDescendingAndAllActiveAndOnePrivateAndAnonymousUser() throws Exception {
        Playlist playlist1 = new Playlist("one");
        Playlist playlist2 = new Playlist("two", false);
        Playlist playlist3 = new Playlist("three");

        playlistRepository.save(playlist1);
        playlistRepository.save(playlist2);
        playlistRepository.save(playlist3);

        Sort sort = Sort.by(Sort.Direction.DESC, "title");
        List<Playlist> playlists = playlistRepository.findAllByIsActiveTrueAndIsPublicTrue(sort);

        assertEquals(playlists.toString(), "[three, one]");
    }

    @Test
    public void testSortPlaylistsByTitleDescendingAndAllActiveAndOnePrivateAndOwner() throws Exception {
        Playlist playlist1 = new Playlist("one");
        Playlist playlist2 = new Playlist("two", "owner",false);
        Playlist playlist3 = new Playlist("three");

        playlistRepository.save(playlist1);
        playlistRepository.save(playlist2);
        playlistRepository.save(playlist3);

        Sort sort = Sort.by(Sort.Direction.DESC, "title");
        List<Playlist> playlists = playlistRepository.findAllByIsActiveTrueAndIsPublicTrueOrIsActiveTrueAndAuthorID("owner", sort);
        System.out.println(playlists.toString());
        assertEquals(playlists.toString(), "[two, three, one]");
    }

    @Test
    public void testSortPlaylistsByCreatedDateAscendingAndAllActiveAndOnePrivateAndAnonymousUser() throws Exception {
        Playlist playlist1 = new Playlist("one");
        Playlist playlist2 = new Playlist("two", false);
        Playlist playlist3 = new Playlist("three");

        playlistRepository.save(playlist1);
        playlistRepository.save(playlist2);
        playlistRepository.save(playlist3);

        Sort sort = Sort.by(Sort.Direction.ASC, "createdDate");
        List<Playlist> playlists = playlistRepository.findAllByIsActiveTrueAndIsPublicTrue(sort);

        assertEquals(playlists.toString(), "[one, three]");
    }

    @Test
    public void testSortPlaylistsByCreatedDateAscendingAndAllActiveAndOnePrivateAndOwner() throws Exception {
        Playlist playlist1 = new Playlist("one");
        Playlist playlist2 = new Playlist("two", "owner",false);
        Playlist playlist3 = new Playlist("three");

        playlistRepository.save(playlist1);
        playlistRepository.save(playlist2);
        playlistRepository.save(playlist3);

        Sort sort = Sort.by(Sort.Direction.ASC, "createdDate");
        List<Playlist> playlists = playlistRepository.findAllByIsActiveTrueAndIsPublicTrueOrIsActiveTrueAndAuthorID("owner", sort);
        System.out.println(playlists.toString());
        assertEquals(playlists.toString(), "[one, two, three]");
    }

    @Test
    public void testSortPlaylistsByFilmsSizeAscendingAndAllActiveAndOnePrivateAndAnonymousUser() throws Exception {
        Playlist playlist1 = new Playlist("one");
        Playlist playlist2 = new Playlist("two", false);
        Playlist playlist3 = new Playlist("three");

        playlistRepository.save(playlist1);
        playlistRepository.save(playlist2);
        playlistRepository.save(playlist3);

        Sort sort = Sort.by(Sort.Direction.ASC, "films");
        List<Playlist> playlists = playlistRepository.findAllByIsActiveTrueAndIsPublicTrue(sort);

        assertEquals(playlists.toString(), "[one, three]");
    }

    @Test
    public void testSortPlaylistsByFilmsSizeAscendingAndAllActiveAndOnePrivateAndOwner() throws Exception {
        Playlist playlist1 = new Playlist("one");
        Playlist playlist2 = new Playlist("two", "owner",false);
        Playlist playlist3 = new Playlist("three");

        playlistRepository.save(playlist1);
        playlistRepository.save(playlist2);
        playlistRepository.save(playlist3);

        Sort sort = Sort.by(Sort.Direction.ASC, "films");
        List<Playlist> playlists = playlistRepository.findAllByIsActiveTrueAndIsPublicTrueOrIsActiveTrueAndAuthorID("owner", sort);
        System.out.println(playlists.toString());
        assertEquals(playlists.toString(), "[one, two, three]");
    }

    @Test(expected = DuplicateKeyException.class)
    public void testSavePlaylistWithExistingTitleAndSameUser() throws Exception {
        playlistRepository.save(new Playlist( "one", "1"));
        playlistRepository.save(new Playlist( "one", "1"));
    }

    @Test
    public void testGetAllPlaylists() {
        playlistRepository.save(new Playlist( "one"));
        playlistRepository.save(new Playlist( "two"));
        ResponseEntity<List<Playlist>> response = new ResponseEntity<>(playlistRepository.findAll(), HttpStatus.OK);
        assertNotNull(response.getBody());
        assertEquals(response.getBody().size(), 2);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
    }

    @Test
    public void testGetPlaylistById() {
        Playlist playlist = new Playlist("one");
        playlist.setId(new ObjectId("ae9ace903d59322d1b45a743"));
        playlistRepository.save(playlist);
        ResponseEntity<Playlist> response = new ResponseEntity<>(playlistRepository.findById(
                playlist.getId()).orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND)), HttpStatus.OK);
        assertNotNull(response.getBody());
        assertEquals(response.getStatusCode(), HttpStatus.OK);
    }

    @Test
    public void testCreatePlaylist() throws Exception {
        ResponseEntity<Playlist> postResponse = new ResponseEntity<>(playlistRepository.save(new Playlist("one")), HttpStatus.OK);
        assertNotNull(postResponse);
        assertNotNull(postResponse.getBody());
        assertEquals(postResponse.getStatusCode(), HttpStatus.OK);
    }

    @Test
    public void testUpdatePlaylistById() {
        Playlist playlist = new Playlist("one");
        playlist.setId(new ObjectId("ae9ace903d59322d1b45a743"));
        playlistRepository.save(playlist);
        playlist = playlistRepository.findById("ae9ace903d59322d1b45a743").orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));
        assertNotNull(playlist);
        List<String> films = new ArrayList<>();
        films.add("1");
        films.add("2");
        playlist.setFilms(films);
        ResponseEntity<Playlist> response = new ResponseEntity<>(playlistRepository.save(playlist), HttpStatus.OK);
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(response.getBody().getFilms().size(), 2);
    }

    @Test
    public void testDeletePlaylistById() {
        Playlist playlist = new Playlist("one");
        playlist.setId(new ObjectId("ae9ace903d59322d1b45a743"));
        playlistRepository.save(playlist);
        playlistRepository.deleteById(playlist.getId());
        try {
            playlist = playlistRepository.findById(playlist.getId()).orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));
        } catch (final HttpClientErrorException e) {
            assertEquals(e.getStatusCode(), HttpStatus.NOT_FOUND);
        }
    }
}
