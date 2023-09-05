#!/bin/bash

# Get applicationId
echo "Enter applicationId"
read applicationId
echo
# Get applicationName
echo "Enter applicationName"
read applicationName
echo

# Trim application name
applicationName=$(echo $applicationName | tr -d '[:space:]')
# Format application name
applicationName=$(echo $applicationName | sed -e 's/\b\(.\)/\u\1/g')

# copy the template to the applicationName directory on the upper level
mkdir "../$applicationName" && cp -r . "../$applicationName"

# rm the initializer.sh file
rm "../$applicationName/initializer.sh"

# rm the gi directory and .gitignore file
rm -rf "../$applicationName/.git"
rm "../$applicationName/.gitignore"

# move the app/src/main/java directory contents to the applicationId directory
mv "../$applicationName/app/src/main/java/com/ytapps/composetemplate/" "../$applicationName/app/src/main/java/$applicationId"

# move the app/src/androidTest/java directory contents to the applicationId directory
mv "../$applicationName/app/src/androidTest/java/com/ytapps/composetemplate/" "../$applicationName/app/src/androidTest/java/$applicationId"

# move the app/src/test/java directory contents to the applicationId directory
mv "../$applicationName/app/src/test/java/com/ytapps/composetemplate/" "../$applicationName/app/src/test/java/$applicationId"

# rm the com directory
rm -rf "../$applicationName/app/src/main/java/com"
rm -rf "../$applicationName/app/src/androidTest/java/com"
rm -rf "../$applicationName/app/src/test/java/com"

# Use find to locate all files recursively
find "../$applicationName" -type f \( -name "*.kt" -o -name "*.xml" -o -name "*.kts" \) -print0 | while IFS= read -r -d $'\0' file; do
    # Replace the string
    sed -i '' "s/com.ytapps.composetemplate/$applicationId/g" "$file"
    sed -i '' "s/ComposeTemplate/$applicationName/g" "$file"
done

# Success message
echo "$applicationName created successfully!"