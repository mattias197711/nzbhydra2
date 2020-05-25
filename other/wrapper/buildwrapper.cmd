@echo off

rem The windows docker (cdrx/pyinstaller-windows) produces unusable results

echo todo Add exe details (author, etc)
pause
pause
pause

echo Running pyInstaller for windows wrapper
c:\programme\Python27\Scripts\pyinstaller.exe -F nzbhydra2wrapper.py -n "NZBHydra2" -i nzbhydra.ico -w --clean --win-private-assemblies
copy dist\nzbhydra2.exe ..\..\releases\windows-release\include / y

echo Running pyInstaller for windows console wrapper
c:\programme\Python27\Scripts\pyinstaller.exe -F nzbhydra2wrapper.py -n "NZBHydra2 Console" -i nzbhydra.ico  --clean --win-private-assemblies
copy "dist\nzbhydra2 console.exe" ..\..\releases\windows-release\include / y

echo Running docker for linux wrapper
docker run -v "C:\Users\strat\IdeaProjects\nzbhydra2\other\wrapper:/src/" cdrx/pyinstaller-linux

echo Copying linux wrapper
copy "dist\linux\NZBHydra2 Console" ..\..\releases\linux-release\include\nzbhydra2 / y
