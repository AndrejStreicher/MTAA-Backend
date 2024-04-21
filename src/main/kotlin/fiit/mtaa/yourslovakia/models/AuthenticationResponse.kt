package fiit.mtaa.yourslovakia.models

data class AuthenticationResponse(
    val accessToken: String,
    val refreshToken: String,
)
