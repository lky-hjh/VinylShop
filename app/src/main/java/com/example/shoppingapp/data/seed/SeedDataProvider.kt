package com.example.shoppingapp.data.seed

import android.content.Context
import com.example.shoppingapp.data.local.dao.ProductDao
import com.example.shoppingapp.data.local.dao.UserDao
import com.example.shoppingapp.data.local.entity.ProductEntity
import com.example.shoppingapp.data.local.entity.UserEntity
import com.example.shoppingapp.data.local.entity.UserRole
import com.example.shoppingapp.util.PasswordUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SeedDataProvider @Inject constructor(
    private val productDao: ProductDao,
    private val userDao: UserDao,
    @ApplicationContext private val context: Context
) {

    suspend fun seedIfNeeded() {
        val prefs = context.getSharedPreferences("vinylshop_prefs", Context.MODE_PRIVATE)
        if (prefs.getBoolean("seed_data_inserted", false)) return

        withContext(Dispatchers.IO) {
            try {
                val existing = productDao.getAllProducts().first()
                if (existing.isEmpty()) {
                    productDao.insertAll(VINYL_RECORDS)
                }

                // Seed default admin user
                val adminUser = userDao.getUserByUsername("admin")
                if (adminUser == null) {
                    val hashedPassword = PasswordUtils.hash("admin123")
                    userDao.insert(
                        UserEntity(
                            id = "user_admin_001",
                            username = "admin",
                            email = "admin@vinylshop.com",
                            password = hashedPassword,
                            role = UserRole.ADMIN,
                            phone = "13800000000",
                            address = "管理员地址"
                        )
                    )
                }

                // Seed mock third-party users (for demo / 考核展示)
                seedThirdPartyUser(userDao, "wechat_mock", "微信用户", "wechat@mock.com")
                seedThirdPartyUser(userDao, "qq_mock", "QQ用户", "qq@mock.com")

                prefs.edit().putBoolean("seed_data_inserted", true).apply()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 为第三方登录创建 mock 用户（若不存在）
     */
    private suspend fun seedThirdPartyUser(
        userDao: UserDao,
        username: String,
        displayName: String,
        email: String
    ) {
        if (userDao.getUserByUsername(username) == null) {
            val hashed = com.example.shoppingapp.util.PasswordUtils.hash("mock123")
            userDao.insert(
                com.example.shoppingapp.data.local.entity.UserEntity(
                    id = "user_${username}_001",
                    username = username,
                    email = email,
                    password = hashed,
                    phone = "",
                    address = "",
                    role = com.example.shoppingapp.data.local.entity.UserRole.USER
                )
            )
        }
    }

    companion object {
        val VINYL_RECORDS = listOf(
            ProductEntity(
                id = "vinyl_001",
                name = "Abbey Road",
                artist = "The Beatles",
                genre = "摇滚",
                price = 299.0,
                description = "披头士乐队的经典专辑，录制于1969年。封面上的艾比路斑马线成为摇滚史上最著名的画面之一。收录了《Come Together》、《Something》等不朽金曲。",
                imageUrl = "https://picsum.photos/seed/vinyl1/400/400",
                rating = 4.9f,
                stock = 50,
                releaseDate = 1577836800000L,
                isFeatured = true,
                isNew = false
            ),
            ProductEntity(
                id = "vinyl_002",
                name = "Kind of Blue",
                artist = "Miles Davis",
                genre = "爵士",
                price = 259.0,
                description = "迈尔斯·戴维斯的杰作，被誉为爵士乐史上最重要的专辑之一。1959年录制，以调式爵士（Modal Jazz）开创了爵士乐的新纪元。",
                imageUrl = "https://picsum.photos/seed/vinyl2/400/400",
                rating = 4.8f,
                stock = 35,
                releaseDate = 1577836800000L,
                isFeatured = true,
                isNew = false
            ),
            ProductEntity(
                id = "vinyl_003",
                name = "Thriller",
                artist = "Michael Jackson",
                genre = "流行",
                price = 329.0,
                description = "流行音乐之王迈克尔·杰克逊的巅峰之作，全球销量最高的专辑。收录了《Thriller》、《Billie Jean》、《Beat It》等超级金曲。",
                imageUrl = "https://picsum.photos/seed/vinyl3/400/400",
                rating = 4.9f,
                stock = 60,
                releaseDate = 1577836800000L,
                isFeatured = true,
                isNew = false
            ),
            ProductEntity(
                id = "vinyl_004",
                name = "The Dark Side of the Moon",
                artist = "Pink Floyd",
                genre = "摇滚",
                price = 319.0,
                description = "平克·弗洛伊德的旷世杰作，以其前卫的音效和深刻的主题著称。三棱镜封面成为摇滚文化的标志。连续741周停留在Billboard榜单上。",
                imageUrl = "https://picsum.photos/seed/vinyl4/400/400",
                rating = 4.9f,
                stock = 45,
                releaseDate = 1577836800000L,
                isFeatured = true,
                isNew = false
            ),
            ProductEntity(
                id = "vinyl_005",
                name = "Rumours",
                artist = "Fleetwood Mac",
                genre = "摇滚",
                price = 279.0,
                description = "Fleetwood Mac的经典专辑，在乐队成员关系紧张的背景下诞生，却成为史上最畅销的专辑之一。《Dreams》等歌曲至今广为传唱。",
                imageUrl = "https://picsum.photos/seed/vinyl5/400/400",
                rating = 4.7f,
                stock = 40,
                releaseDate = 1577836800000L,
                isFeatured = false,
                isNew = false
            ),
            ProductEntity(
                id = "vinyl_006",
                name = "Random Access Memories",
                artist = "Daft Punk",
                genre = "电子",
                price = 349.0,
                description = "法国电子二人组Daft Punk的格莱美年度专辑。融合了放克、迪斯科和电子音乐，与众多传奇音乐人合作，包括《Get Lucky》等热门单曲。",
                imageUrl = "https://picsum.photos/seed/vinyl6/400/400",
                rating = 4.8f,
                stock = 30,
                releaseDate = 1704067200000L,
                isFeatured = true,
                isNew = false
            ),
            ProductEntity(
                id = "vinyl_007",
                name = "Blue Train",
                artist = "John Coltrane",
                genre = "爵士",
                price = 269.0,
                description = "约翰·科尔特兰在Blue Note厂牌的唯一专辑，硬波普爵士的巅峰之作。标题曲《Blue Train》是科尔特兰最著名的作品之一。",
                imageUrl = "https://picsum.photos/seed/vinyl7/400/400",
                rating = 4.7f,
                stock = 20,
                releaseDate = 1577836800000L,
                isFeatured = false,
                isNew = false
            ),
            ProductEntity(
                id = "vinyl_008",
                name = "21",
                artist = "Adele",
                genre = "流行",
                price = 289.0,
                description = "阿黛尔的突破性专辑，以灵魂乐般的声音和真挚的情感打动了全球听众。《Rolling in the Deep》、《Someone Like You》等歌曲创造了无数记录。",
                imageUrl = "https://picsum.photos/seed/vinyl8/400/400",
                rating = 4.6f,
                stock = 55,
                releaseDate = 1577836800000L,
                isFeatured = false,
                isNew = false
            ),
            ProductEntity(
                id = "vinyl_009",
                name = "In Rainbows",
                artist = "Radiohead",
                genre = "另类",
                price = 299.0,
                description = "Radiohead的第七张录音室专辑，采用\"随你付\"模式发行，开创了音乐发行的新模式。以温暖有机的音色探索了爱与迷失的主题。",
                imageUrl = "https://picsum.photos/seed/vinyl9/400/400",
                rating = 4.7f,
                stock = 25,
                releaseDate = 1577836800000L,
                isFeatured = false,
                isNew = false
            ),
            ProductEntity(
                id = "vinyl_010",
                name = "The Miseducation of Lauryn Hill",
                artist = "Lauryn Hill",
                genre = "嘻哈",
                price = 269.0,
                description = "Lauryn Hill的首张个人专辑，融合了嘻哈、灵魂乐和雷鬼。获格莱美年度专辑奖，是女性嘻哈艺术家的里程碑之作。",
                imageUrl = "https://picsum.photos/seed/vinyl10/400/400",
                rating = 4.8f,
                stock = 15,
                releaseDate = 1577836800000L,
                isFeatured = false,
                isNew = false
            ),
            ProductEntity(
                id = "vinyl_011",
                name = "Motomami",
                artist = "Rosalía",
                genre = "拉丁",
                price = 319.0,
                description = "西班牙歌手Rosalía的突破性专辑，融合了弗拉明戈、雷鬼顿和实验流行。获得了2022年拉丁格莱美年度专辑奖。",
                imageUrl = "https://picsum.photos/seed/vinyl11/400/400",
                rating = 4.5f,
                stock = 35,
                releaseDate = 1717200000000L,
                isFeatured = false,
                isNew = true
            ),
            ProductEntity(
                id = "vinyl_012",
                name = "Did You Know That There's a Tunnel Under Ocean Blvd",
                artist = "Lana Del Rey",
                genre = "另类",
                price = 309.0,
                description = "Lana Del Rey的第九张专辑，延续了她梦幻忧伤的\"好莱坞悲核\"风格。由Jack Antonoff制作，获格莱美年度专辑提名。",
                imageUrl = "https://picsum.photos/seed/vinyl12/400/400",
                rating = 4.4f,
                stock = 40,
                releaseDate = 1717200000000L,
                isFeatured = true,
                isNew = true
            ),
            ProductEntity(
                id = "vinyl_013",
                name = "SOS",
                artist = "SZA",
                genre = "R&B",
                price = 299.0,
                description = "SZA的第二张专辑，融合了R&B、流行和嘻哈元素。专辑在Billboard 200上停留时间创造了历史记录，收录了《Kill Bill》等热单。",
                imageUrl = "https://picsum.photos/seed/vinyl13/400/400",
                rating = 4.6f,
                stock = 45,
                releaseDate = 1717200000000L,
                isFeatured = false,
                isNew = true
            ),
            ProductEntity(
                id = "vinyl_014",
                name = "Un Verano Sin Ti",
                artist = "Bad Bunny",
                genre = "拉丁",
                price = 289.0,
                description = "Bad Bunny的夏季专辑，融合了雷鬼顿、dembow和加勒比节奏。2022年全球最畅销专辑，展现了拉丁音乐的全球影响力。",
                imageUrl = "https://picsum.photos/seed/vinyl14/400/400",
                rating = 4.5f,
                stock = 30,
                releaseDate = 1717200000000L,
                isFeatured = false,
                isNew = true
            ),
            ProductEntity(
                id = "vinyl_015",
                name = "Currents",
                artist = "Tame Impala",
                genre = "电子",
                price = 279.0,
                description = "Kevin Parker的个人项目Tame Impala的第三张专辑，以迷幻流行和合成器驱动的声音为特色。《Let It Happen》等歌曲成为独立音乐的经典。",
                imageUrl = "https://picsum.photos/seed/vinyl15/400/400",
                rating = 4.7f,
                stock = 25,
                releaseDate = 1577836800000L,
                isFeatured = false,
                isNew = false
            ),
            ProductEntity(
                id = "vinyl_016",
                name = "folklore",
                artist = "Taylor Swift",
                genre = "另类",
                price = 339.0,
                description = "Taylor Swift在疫情期间创作的内省专辑，风格转向独立民谣和另类摇滚。与The National的Aaron Dessner合作，获格莱美年度专辑。",
                imageUrl = "https://picsum.photos/seed/vinyl16/400/400",
                rating = 4.8f,
                stock = 70,
                releaseDate = 1704067200000L,
                isFeatured = true,
                isNew = false
            ),
            ProductEntity(
                id = "vinyl_017",
                name = "Igor",
                artist = "Tyler, the Creator",
                genre = "嘻哈",
                price = 289.0,
                description = "Tyler, the Creator的第五张专辑，以独特的制作和叙事风格获格莱美最佳说唱专辑。融合了灵魂乐、放克和新灵魂乐元素。",
                imageUrl = "https://picsum.photos/seed/vinyl17/400/400",
                rating = 4.6f,
                stock = 20,
                releaseDate = 1577836800000L,
                isFeatured = false,
                isNew = false
            ),
            ProductEntity(
                id = "vinyl_018",
                name = "Hit Me Hard and Soft",
                artist = "Billie Eilish",
                genre = "流行",
                price = 329.0,
                description = "Billie Eilish的第三张录音室专辑，由哥哥FINNEAS制作。展现了更加成熟和内省的声音，封面为Billie沉入水中的艺术照。",
                imageUrl = "https://picsum.photos/seed/vinyl18/400/400",
                rating = 4.4f,
                stock = 55,
                releaseDate = 1717200000000L,
                isFeatured = false,
                isNew = true
            )
        )
    }
}
