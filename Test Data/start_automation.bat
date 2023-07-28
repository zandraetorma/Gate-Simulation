@echo off
:: This is the header of Gate Simulation Batch File
echo ****************************************
echo *                                      *
echo * This is an Automation Batch for Gate *
echo *                                      *
echo ****************************************
echo *


echo ***** STARTING EXECUTION *****
cd C:\AutoTest\apps\Katalon\kre

katalonc -noSplash -runMode=console -projectPath="C:\Katalon Automation Tool\Gate-Simulation\Gate Simulation.prj" -retry=0 -testSuitePath="Test Suites/Gate Simulation - LQNQA" -browserType="Chrome" -apiKey="83394771-0db3-4815-8183-0140ae460b42" --config -proxy.auth.option=NO_PROXY -proxy.system.option=NO_PROXY -proxy.system.applyToDesiredCapabilities=true -webui.autoUpdateDrivers=true
echo ***** COMPLETED EXECUTION *****

echo *************** REACHED END ***************
pause