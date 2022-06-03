#include <iostream>
#include <fstream>
#include <vector>
#include <ctime>

using namespace std;

int main(int argc, char const *argv[]) {
    cout << "Content-type: text/html\n\n";

    // Open back-end file and store data in an array
    ifstream inputFile;
    inputFile.open("back_end.txt");

    string line;
    std::vector<string> arr;
    while (getline(inputFile, line)) 
        arr.push_back(line);

    //  Get UTC time and timezone value
    time_t rawtime;
    struct tm * UTC;
    time (&rawtime);
    UTC = gmtime (&rawtime);
    int GMT = stoi(arr.at(0));

    string hours = UTC->tm_hour+GMT < 10 ? ("0" + to_string(UTC->tm_hour + GMT)) : to_string(UTC->tm_hour + GMT);
    string minutes = UTC->tm_min < 10 ? ("0" + to_string(UTC->tm_min)) : to_string(UTC->tm_min);
    string seconds = UTC->tm_sec < 10 ? ("0" + to_string(UTC->tm_sec)) : to_string(UTC->tm_sec);
    string time = hours + ":" + minutes + ":" + seconds;

    //  HTML format
    cout << "<!DOCTYPE html>" << endl;
    cout << "<html lang=\"en\">" << endl;
        cout << "<head>" << endl;
            cout << "<link href='https://fonts.googleapis.com/css?family=JetBrains Mono' rel='stylesheet'>" << endl;
            cout << "<style>" << endl;
                cout << "body { font-family: 'JetBrains Mono';font-size: 22px; }" << endl;
            cout << "</style>" << endl;
            cout << "<meta charset=\"UTF-8\">" << endl;
            cout << "<title>First CGI</title>" << endl;
        cout << "</head>" << endl;
        cout << "<body>" << endl;
            cout << "<center><h1>Current Time " << arr.at(1) << ": " <<  time << "</h1></center><br>" << endl;
            cout << "<center><a href=\"south_africa.cgi\">Switch to South African Time</a></center>" << "<br>" << endl;
            cout << "<center><a href=\"ghana.cgi\">Switch to Ghana Time</a></center>" << endl;
        cout << "</body>" << endl;
    cout << "</html>" << endl;

    // Close the file
    inputFile.close();
    return 0;
}