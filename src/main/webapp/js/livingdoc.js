function openWin(url, winname)
{
    var widthL;
    var heightL;
    var widthW = 800;
    var heightW = 600;

    if (typeof( window.innerWidth ) == 'number')
    {
        widthL = (window.innerWidth - widthW) / 2;
        heightL = (window.innerHeight - heightW) / 2;
    }
    else if (document.documentElement &&
             ( document.documentElement.clientWidth || document.documentElement.clientHeight ))
    {
        widthL = (document.documentElement.clientWidth - widthW) / 2;
        heightL = (document.documentElement.clientHeight - heightW) / 2;
    }
    else if (document.body && ( document.body.clientWidth || document.body.clientHeight ))
    {
        widthL = (document.body.clientWidth - widthW) / 2;
        heightL = (document.body.clientHeight - heightW) / 2;
    }

    var newWin = window.open(url, winname, "left=" + widthL + ",top=" + heightL + ",width=" + widthW + ",height=" +
                                           heightW + ",resizable=1,toolbar=0,scrollbars=1,location=0,menubar=0");
    if (window.focus) { newWin.focus(); }
}
