# Offloading
This project was created as a software 7th project at Aalborg University. Alongside the program a report was created and can found in this project.
The project set out to create a server capable of distributing and offloading programs to mobile phones. 
In addition to the server an [employer (Desktop) client](https://github.com/philipholler/OffloadTornadoClient) and a [worker (Android) client](https://github.com/MagnusKJensen/termux-bridged) was created.

# Running the program

Inside the .run folder are Maven run configurations used for testing and running the program. 
The database and server configurations can be changed inside the src/main/resources/application.properties and src/main/resources/openapi-definition.yaml files.

Make sure to run the ***clean compile generate server*** run configuration as the first to generate the needed files for the server using the openapi generator tool.

# Contributors

Philip Irming Holler - philipholler94@gmail.com

Magnus Kirkegaard Jensen - magnje17@student.aau.dk

Mads Faber - mfaber17@student.aau.dk

Laurits Brøcker - lbrack17@student.aau.dk

# License
MIT License

Copyright (c) 2020 Aalborg University

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
