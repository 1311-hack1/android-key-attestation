import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import com.google.android.attestation.ParsedAttestationRecord
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.util.Base64

@Serializable
data class VerifyRequest(
    val attestationChainPem: List<String>, // array of PEM certs
    val challenge: String
)

@Serializable
data class VerifyResponse(
    val ok: Boolean,
    val packageName: String? = null,
    val signingCertDigest: String? = null,
    val verifiedBootState: String? = null,
    val error: String? = null
)

fun main() {
    embeddedServer(Netty, port = System.getenv("PORT")?.toInt() ?: 8080) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
            })
        }
        
        routing {
            post("/verify") {
                try {
                    val body = call.receiveText()
                    val request = Json.decodeFromString<VerifyRequest>(body)

                    // Parse cert chain
                    val certFactory = CertificateFactory.getInstance("X.509")
                    val certs = request.attestationChainPem.map {
                        certFactory.generateCertificate(it.byteInputStream()) as X509Certificate
                    }

                    // The leaf certificate contains the attestation extension
                    val attestation = ParsedAttestationRecord.createParsedAttestationRecord(certs)

                    // Check challenge
                    val challengeBytes = Base64.getDecoder().decode(request.challenge)
                    val matchesChallenge = attestation.attestationChallenge.toByteArray().contentEquals(challengeBytes)

                    if (!matchesChallenge) {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            VerifyResponse(ok = false, error = "Challenge mismatch")
                        )
                        return@post
                    }

                    // Get attestation application ID from software or TEE enforced list
                    val appId = attestation.softwareEnforced().attestationApplicationId()
                        .or { attestation.teeEnforced().attestationApplicationId() }

                    call.respond(
                        HttpStatusCode.OK,
                        VerifyResponse(
                            ok = true,
                            packageName = appId.map { it.packageInfos().firstOrNull()?.packageName() }.orElse(null),
                            signingCertDigest = appId.map { it.signatureDigests().joinToString { digest -> digest.toStringUtf8() } }.orElse(null),
                            verifiedBootState = attestation.teeEnforced().rootOfTrust().map { it.verifiedBootState().toString() }.orElse("UNKNOWN")
                        )
                    )
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        VerifyResponse(ok = false, error = e.message)
                    )
                }
            }
        }
    }.start(wait = true)
}
