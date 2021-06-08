# BookFinderClient
It's a command line tool supporting functions lie search, sort, find results by page.
The client app will accept the following command line arguments:

--help 
Output a usage message and exit

-s, --search TERM SEARCHTYPE 
Search the Goodreads' API and display the results on screen. 
E.g. -s Zelazny auhtor 
Results will include author, title, and a link or display of the image of the book

--sort FIELD 
Sort previous results by field "author" or "title". If no sort is specified, title is the default
E.g. --sort author

-p NUMBER 
Display the _NUMBER_ page of results

-h, --host 
Return the hostname or ip address where the server can be found
