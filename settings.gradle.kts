rootProject.name = "foxden"

// BOM module
include("foxden-bom")

// Common modules
include("foxden-common")
include("foxden-common:foxden-common-core")
include("foxden-common:foxden-common-jimmer")
include("foxden-common:foxden-common-web")
include("foxden-common:foxden-common-security")
include("foxden-common:foxden-common-email")

// Domain modules
include("foxden-domain")
include("foxden-domain:foxden-domain-system")
include("foxden-domain:foxden-domain-tenant")
include("foxden-domain:foxden-domain-infrastructure")

// Application modules
include("foxden-app:foxden-app-admin")
