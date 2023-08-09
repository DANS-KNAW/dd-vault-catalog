mvn dans-build-resources:get-helper-script
sh target/add-swagger-ui.sh

mvn initialize
cp target/openapi/dd-vault-catalog-api.yml docs/api.yml


