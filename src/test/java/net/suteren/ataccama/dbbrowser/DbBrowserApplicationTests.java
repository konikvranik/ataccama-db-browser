package net.suteren.ataccama.dbbrowser;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.suteren.ataccama.dbbrowser.entity.connections.ConnectionRecord;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureRestDocs
public class DbBrowserApplicationTests {

	@Rule
	public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation("build/generated-snippets");

	@Autowired
	private WebApplicationContext context;

	private MockMvc mockMvc = MockMvcBuilders.standaloneSetup()
			.alwaysDo(document("{method-name}",
					preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())))
			.build();

	@Autowired
	private ObjectMapper objectMapper;

	@Before
	public void setUp() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
				.apply(documentationConfiguration(this.restDocumentation))
				.build();
	}

	@Test
	public void testIndex() throws Exception {
		this.mockMvc.perform(get("/")).andExpect(status().isOk())
				.andDo(document("index",
						links(
								linkWithRel("connectionRecords").description("The CRUD resource of connection records"),
								linkWithRel("profile").description("Resources description")),
						responseFields(subsectionWithPath("_links")
								.description("Links to other resources")),
						responseHeaders(headerWithName("Content-Type")
								.description("The Content-Type of the payload"))))
		;
	}

	@Test
	public void testConnectionRecordsCreate() throws Exception {

		ConnectionRecord cr = new ConnectionRecord();
		cr.setName("test");
		cr.setHostname("host");
		cr.setPort(13);
		cr.setDatabaseName("database");
		cr.setUsername("user");
		cr.setPassword("password");

		this.mockMvc.perform(post("/connectionRecords").contentType(MediaTypes.HAL_JSON)
				.content(this.objectMapper.writeValueAsString(cr)))
				.andExpect(status().isCreated())
				.andDo(document("connectionRecordsCreate",
						requestFields(
								fieldWithPath("id").description("The id of the newly created record"),
								fieldWithPath("name").description("Reference name od database connection"),
								fieldWithPath("hostname").description("Hostname of the MySQL server"),
								fieldWithPath("port").description("POrt of the MySQL server"),
								fieldWithPath("databaseName").description("Database to connect to"),
								fieldWithPath("username").description("Database user"),
								fieldWithPath("password").description("Database password")
						)));

		this.mockMvc.perform(get("/connectionRecords/1").contentType(MediaTypes.HAL_JSON)
				.content(this.objectMapper.writeValueAsString(cr)))
				.andExpect(status().isOk())
				.andDo(document("connectionRecordsRead",
						responseFields(
								fieldWithPath("name").description("Reference name od database connection"),
								fieldWithPath("hostname").description("Hostname of the MySQL server"),
								fieldWithPath("port").description("POrt of the MySQL server"),
								fieldWithPath("databaseName").description("Database to connect to"),
								fieldWithPath("username").description("Database user"),
								fieldWithPath("password").description("Database password"),
								subsectionWithPath("_links").description("<<connectionRecordsRead-index-links,Links>> to other resources")
						)
				));

		this.mockMvc.perform(put("/connectionRecords/1").contentType(MediaTypes.HAL_JSON)
				.content(this.objectMapper.writeValueAsString(cr)))
				.andExpect(status().isNoContent())
				.andDo(document("connectionRecordsUpdate",
						requestFields(
								fieldWithPath("id").description("The id of the newly created record"),
								fieldWithPath("name").description("Reference name od database connection"),
								fieldWithPath("hostname").description("Hostname of the MySQL server"),
								fieldWithPath("port").description("POrt of the MySQL server"),
								fieldWithPath("databaseName").description("Database to connect to"),
								fieldWithPath("username").description("Database user"),
								fieldWithPath("password").description("Database password")
						)));

		this.mockMvc.perform(delete("/connectionRecords/1").contentType(MediaTypes.HAL_JSON)
				.content(this.objectMapper.writeValueAsString(cr)))
				.andExpect(status().isNoContent())
				.andDo(document("connectionRecordsDelete"));
	}
}
