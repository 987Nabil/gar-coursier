package coursier.cache.protocol

import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.http.{
  ByteArrayContent,
  GenericUrl,
  HttpRequestFactory,
  HttpResponseException,
}
import com.google.auth.http.HttpCredentialsAdapter
import com.google.auth.oauth2.GoogleCredentials

import java.io.{ByteArrayOutputStream, InputStream, OutputStream}
import java.net.{HttpURLConnection, URL, URLConnection, URLStreamHandler, URLStreamHandlerFactory}
import scala.jdk.CollectionConverters.SetHasAsJava

class ArtifactregistryHandler extends URLStreamHandlerFactory {

  override def createURLStreamHandler(protocol: String): URLStreamHandler =
    if (protocol == "artifactregistry") new ArtifactregistryStreamHandler
    else null

}

class ArtifactregistryStreamHandler extends URLStreamHandler {

  override def openConnection(url: URL): URLConnection =
    new GcsArtifactRegistryUrlConnection(requestFactory, url)

  private def requestFactory: HttpRequestFactory =
    new NetHttpTransport().createRequestFactory(new HttpCredentialsAdapter(credentials))

  private def credentials: GoogleCredentials =
    GoogleCredentials
      .getApplicationDefault()
      .createScoped(
        Set(
          "https://www.googleapis.com/auth/cloud-platform",
          "https://www.googleapis.com/auth/cloud-platform.read-only",
        ).asJava
      )

}

class GcsArtifactRegistryUrlConnection(googleHttpRequestFactory: HttpRequestFactory, url: URL)
    extends HttpURLConnection(url) {

  override def connect(): Unit = {
    connected = false
    try {
      connected = googleHttpRequestFactory.buildHeadRequest(genericUrl(url)).execute().isSuccessStatusCode
    } catch {
      case ex: HttpResponseException =>
        responseCode = ex.getStatusCode
        responseMessage = ex.getStatusMessage
    }
  }

  override def getInputStream: InputStream =
    try {
      if (!connected) {
        connect()
      }
      googleHttpRequestFactory.buildGetRequest(genericUrl(url)).execute().getContent
    } catch {
      case ex: HttpResponseException =>
        responseCode = ex.getStatusCode
        responseMessage = ex.getStatusMessage
        null
    }

  override def getOutputStream: OutputStream = {
    if (!connected) {
      connect()
    }
    new ByteArrayOutputStream() {
      override def close(): Unit = {
        super.close()
        googleHttpRequestFactory
          .buildPutRequest(
            genericUrl(url),
            new ByteArrayContent(getRequestProperty("Content-Type"), toByteArray),
          )
          .execute()
      }
    }
  }

  override def disconnect(): Unit    = connected = false
  override def usingProxy(): Boolean = false

  private def genericUrl(url: URL): GenericUrl = {
    val genericUrl = new GenericUrl()
    genericUrl.setScheme("https")
    genericUrl.setHost(url.getHost)
    genericUrl.appendRawPath(url.getPath)
    genericUrl
  }

}
