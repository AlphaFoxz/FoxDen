rootProject.name = "foxden"

// BOM module
include("foxden-bom")

// Common modules
include("foxden-common")
include("foxden-common:foxden-common-core")
include("foxden-common:foxden-common-json")
include("foxden-common:foxden-common-jimmer")
include("foxden-common:foxden-common-web")
include("foxden-common:foxden-common-security")
include("foxden-common:foxden-common-redis")
include("foxden-common:foxden-common-log")
include("foxden-common:foxden-common-oss")
include("foxden-common:foxden-common-excel")
include("foxden-common:foxden-common-idempotent")
include("foxden-common:foxden-common-mail")
include("foxden-common:foxden-common-doc")
include("foxden-common:foxden-common-sms")
include("foxden-common:foxden-common-ratelimiter")
include("foxden-common:foxden-common-social")
include("foxden-common:foxden-common-sse")
include("foxden-common:foxden-common-encrypt")

// Domain modules
include("foxden-domain")
include("foxden-domain:foxden-domain-system")
include("foxden-domain:foxden-domain-tenant")
include("foxden-domain:foxden-domain-infrastructure")
include("foxden-domain:foxden-domain-workflow")
include("foxden-domain:foxden-domain-gen")
include("foxden-domain:foxden-domain-test")

// Application modules
include("foxden-app:foxden-app-admin")
include("foxden-app:foxden-app-system")
include("foxden-app:foxden-app-workflow")
