import java.util.*;
import java.io.*;


class MusicSearch
{

    public static void main( String args[] )
    {

// - Variables - //


        // Subscriptions File
        String subFileName = "C:\\Users\\James\\Google Drive\\#Coding\\Java\\#Data\\Subscriptions.txt";
        SubscriptionsReader subReader = null;
        String subscription = null;
        HashMap<String, String> subscriptionData = null;
        // NewMusic File
        String newMusicFileName = "C:\\Users\\James\\Google Drive\\#Coding\\Java\\#Data\\NewMusic.txt";
        MusicSaver musicSaver = null;
        // YouTube Scraper
        YouTubeChannelScraper youtubeScraper = new YouTubeChannelScraper();
        LinkedList<String> songsList = null;
        int numConnectTries = 10;
        // Output dialog box
        OutputBox outputBox = new OutputBox( "Music Search", true );


// - Main - //


        try
        {
            
            // Initialising variables
            subReader = new SubscriptionsReader( subFileName );
            musicSaver = new MusicSaver( newMusicFileName );

            // WHILE not at end of list of subscriptions
            while ( !subReader.endOfSubscriptions() )
            {

                // PRINT OutputBox heading
                outputBox.newHeading();

                // Get subscription
                subscription = subReader.getNextSubscription();

                try
                {

                    // IF is not a new subscription, get new songs
                    if ( !subReader.isNewSub( subscription ) )
                    {
                        // Get the subscription's data
                        subscriptionData = subReader.getSubscriptionsData( subscription );

                        // PRINT 'checking channel' message with name of channel
                        outputBox.update( "Checking channel: " + subscriptionData.get("Name") + "..." );

                        // Set youtube scraper's channel, then get list of new songs
                        youtubeScraper.setChannel( subscriptionData.get( "URL" ), numConnectTries );
                        songsList = youtubeScraper.getNewSongs( subscriptionData.get( "LatestHref" ) );

                        // Add songs to 'NewMusic' save (doesn't add if no songs)
                        musicSaver.addSongs( songsList );

                        // Create updated subscription to add to list of updated subsscriptions later
                        subscription = youtubeScraper.createSubscription();

                        // PRINT subscription completed message depending on how many songs found
                        printSubscriptionSuccess( outputBox, songsList.size() );

                    }
                    // ELSE IF new, just create subscription
                    else
                    {

                        // Set youtube scraper's channel, then create a subscription from this
                        youtubeScraper.setChannel( subscription, numConnectTries );
                        subscription = youtubeScraper.createSubscription();

                        // PRINT 'created subscription' message with name of channel
                        subscriptionData = subReader.getSubscriptionsData( subscription );
                        outputBox.update( "Created new subscription: " + subscriptionData.get("Name") );

                    }

                    // Add to sub reader's list of updated subscriptions
                    subReader.addUpdatedSubscription( subscription );

                }
                catch( IOException e )
                {
                    outputBox.update( "Error with I/O!" );
                }

            }

            // Update new 'Subscriptions.txt'
            outputBox.newHeading();
            outputBox.update( "Updating subscriptions file..." );
            subReader.updateSubscriptions( subFileName );

            // Final success message
            printFinalSuccess( outputBox );

            // Close music saver's output
            musicSaver.close();
        
        }
        catch ( IOException e )
        {
            outputBox.update("Error with I/O!");
        }
    
    }


// - Message Printing - //


    // When finished each subscription
    private static void printSubscriptionSuccess( OutputBox inBox, int inNumSongsFound )
    {

        // IF didn't get any new songs
        if ( inNumSongsFound == 0 )
        {
            inBox.update( "No new videos." );
        }
        // ELSE IF got 1
        else if ( inNumSongsFound == 1 )
        {
            inBox.update( "Found 1 new video!" );
        }
        // ELSE if got multiple
        else
        {
            inBox.update( "Found " + ( inNumSongsFound ) + " new videos!" );
        }

    }


    // When finished everything
    private static void printFinalSuccess( OutputBox inBox )
    {
        inBox.newHeading();
        inBox.update( "All done!" );
    }


}
